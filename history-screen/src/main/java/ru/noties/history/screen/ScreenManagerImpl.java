package ru.noties.history.screen;

import android.app.Activity;
import android.app.Application;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.history.Subscription;
import ru.noties.history.screen.plugin.Plugin;
import ru.noties.history.screen.transition.Transition;
import ru.noties.history.screen.transition.TransitionController;

class ScreenManagerImpl<K extends Enum<K>> extends ScreenManager<K> implements History.Observer<K> {

    // todo: validate that createView does not modify container (at least for children count...), but do we need it actually?
    // todo: getItem|Screen method
    // todo: force main thread
    // todo: maybe we could instead of the whole history supply the observer... so we could ignore some keys (mock entries)
    //      - the thing is: we won't allow mocking of items, but allow filtering history before saving

    private final History<K> history;

    private final ScreenProvider<K> screenProvider;

    private final List<ScreenManagerItem<K>> items = new ArrayList<>(3);

    private final Subscription historySubscription;

    private final TransitionController<K> transitionController;

    private final ScreenManagerEventDispatcher<K> eventDispatcher = new ScreenManagerEventDispatcher<>();

    private final VisibilityProvider<K> visibilityProvider;

    @Nullable
    private final LayoutInflater layoutInflater;

    private final boolean detachLastView;

    private final Map<Class<? extends Plugin>, Plugin> plugins;

    private final TransitionLock transitionLock;


    private Transition.Callback pendingTransition;

    private boolean pendingTransitionCancelled;

    private Activity activity;

    private ViewGroup container;

    private boolean restoring;


    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    private View.OnAttachStateChangeListener containerOnAttachStateListener;

    private boolean activityResumed;


    ScreenManagerImpl(
            @NonNull Activity activity,
            @NonNull ViewGroup container,
            @NonNull History<K> history,
            @NonNull ScreenProvider<K> screenProvider,
            @NonNull TransitionController<K> transitionController,
            @NonNull VisibilityProvider<K> visibilityProvider,
            @Nullable LayoutInflater inflater,
            boolean detachLastView,
            @NonNull Map<Class<? extends Plugin>, Plugin> plugins,
            @NonNull TransitionLock transitionLock
    ) {
        this.activity = activity;
        this.container = container;
        this.history = history;
        this.screenProvider = screenProvider;
        this.historySubscription = history.observe(this);
        this.transitionController = transitionController;
        this.visibilityProvider = visibilityProvider;
        this.layoutInflater = inflater;
        this.detachLastView = detachLastView;
        this.plugins = plugins;
        this.transitionLock = transitionLock;

        listenForActivityEvents();
        listenForContainerEvents();
    }

    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull final Entry<K> current) {

        // ignore this call if we are restoring state (applicable only for push event)
        // state restoration should take care of adding views to container as appropriate
        if (restoring) {
            return;
        }

        replaceScreen(previous, current, new ReplaceEndAction<K>() {
            @Override
            void run(@NonNull ScreenManagerItem<K> previousItem) {

                final Visibility visibility = previousItem.visibility = resolveInActiveVisibility(previousItem.entry, current);

                if (visibility == null) {

                    detach(previousItem);

                } else {

                    // just accept the visibility to a view (please note that onDetach event is not dispatched)
                    previousItem.view.setVisibility(visibility.androidViewVisibility());
                }
            }
        });
    }

    @Override
    public void onEntryReplaced(@Nullable final Entry<K> previous, @NonNull Entry<K> current) {

        replaceScreen(previous, current, new ReplaceEndAction<K>() {
            @Override
            void run(@NonNull ScreenManagerItem<K> previousItem) {

                detach(previousItem);

                destroy(previousItem);
            }
        });
    }

    private void replaceScreen(@Nullable Entry<K> previous, @NonNull final Entry<K> current, @NonNull final ReplaceEndAction<K> endAction) {

        cancelPendingTransition();

        // previous will be null only if it's the first operation and history is currently empty

        final ScreenManagerItem<K> previousItem;

        if (previous != null) {

            // push/replace operation can only happen on top of currently active item
            previousItem = lastItem();

            // send inactive event
            inactive(previousItem);

        } else {
            previousItem = null;
        }

        // obtain previous (actually `current`) view to accept modifications
        final View previousView = previousItem != null
                ? previousItem.view
                : null;

        final ScreenManagerItem<K> currentItem = init(current);

        attach(currentItem);

        transitionLock.lock();

        // execute transition between previous and current
        pendingTransition = transitionController.forward(previous, current, previousView, currentItem.view, new Runnable() {
            @Override
            public void run() {

                pendingTransition = null;

                transitionLock.unlock();

                if (!pendingTransitionCancelled
                        && activityResumed) {
                    // also: we must check if activity is in resumed state,
                    // so we do not make screen active when activity is not
                    active(currentItem);
                }

                // only run supplied action if we have previous item
                if (previousItem != null) {
                    endAction.run(previousItem);
                }
            }
        });
    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {

        cancelPendingTransition();

        final ScreenManagerItem<K> poppedItem = lastItem();

        inactive(poppedItem);

        // now, obtain item to appear
        final ScreenManagerItem<K> toAppearItem = onPoppedToAppear(toAppear, items.size() - 2);

        final View toAppearView = toAppearItem != null
                ? toAppearItem.view
                : null;

        transitionLock.lock();

        pendingTransition = transitionController.back(popped, toAppear, poppedItem.view, toAppearView, new Runnable() {
            @Override
            public void run() {

                pendingTransition = null;

                transitionLock.unlock();

                if (toAppearItem != null
                        && !pendingTransitionCancelled
                        && activityResumed) {
                    active(toAppearItem);
                }

                detach(poppedItem, toAppearItem == null && !detachLastView);

                destroy(poppedItem);
            }
        });
    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {

        cancelPendingTransition();

        // eval all popped items -> walk the lifecycle, destroy
        // animate from first popped to toAppear

        // so, let's extract items that we want to
        final List<ScreenManagerItem<K>> poppedItems = toAppear == null
                ? items
                : items.subList(items.size() - popped.size(), items.size());

        final ScreenManagerItem<K> poppedTopItem = lastItem();

        // make it inactive
        inactive(poppedTopItem);

        final View viewForPoppedItems = viewForPoppedItems(poppedItems);

        final ScreenManagerItem<K> toAppearItem = onPoppedToAppear(toAppear, items.size() - popped.size() - 1);

        final View toAppearView = toAppearItem != null
                ? toAppearItem.view
                : null;

        transitionLock.lock();

        pendingTransition = transitionController.back(poppedTopItem.entry, toAppear, viewForPoppedItems, toAppearView, new Runnable() {
            @Override
            public void run() {

                pendingTransition = null;

                transitionLock.unlock();

                if (toAppearItem != null
                        && !pendingTransitionCancelled
                        && activityResumed) {
                    active(toAppearItem);
                }

                destroyPopped(toAppearItem, viewForPoppedItems);
            }
        });
    }

    private void cancelPendingTransition() {

        if (pendingTransition != null) {
            pendingTransitionCancelled = true;
            pendingTransition.cancel();

            transitionLock.unlock();
        }

        // clear this flag no matter of pendingTransition state
        pendingTransitionCancelled = false;
    }

    @NonNull
    private ScreenManagerItem<K> lastItem() {
        return items.get(items.size() - 1);
    }

    @Nullable
    private Visibility resolveInActiveVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active) {
        return visibilityProvider.resolveInActiveVisibility(toResolve, active);
    }

    private int actualViewIndexForItem(@NonNull ScreenManagerItem<K> toFind) {

        int index = -1;

        boolean found = false;

        for (ScreenManagerItem<K> item : items) {

            if (item == toFind) {
                index += 1;
                found = true;
                break;
            }

            if (item.visibility != null) {
                index += 1;
            }
        }

        return found ? index : -1;
    }

    @NonNull
    @Override
    public Activity activity() {
        return activity;
    }

    @NonNull
    @Override
    public History<K> history() {
        return history;
    }

    @NonNull
    @Override
    public Subscription screenCallbacks(@NonNull ScreenLifecycleCallbacks<K> screenLifecycleCallbacks) {
        return eventDispatcher.callbacks(screenLifecycleCallbacks);
    }

    @NonNull
    @Override
    public ScreenLifecycle screenLifecycle(@NonNull Screen<K, ? extends Parcelable> screen) {
        return eventDispatcher.lifecycle(screen);
    }

    @Override
    public boolean restoreState(@Nullable HistoryState state) {

        final boolean result;

        if (state == null) {

            result = false;

        } else {

            restoring = true;
            result = history.restore(state);
            restoring = false;

            if (result) {

                // create all items first
                for (Entry<K> entry : history.entries()) {
                    init(entry);
                }

                // now, as we do not save previous visibility, we will resolve it now with our
                // visibility provider
                //
                // if we have only 1 item: just make it visible
                // if we have more -> start with 1 one and resolve by referencing previous

                final int size = items.size();

                // this should always be true, but anyway
                if (size > 0) {

                    if (size > 1) {

                        ScreenManagerItem<K> previous = items.get(0);
                        ScreenManagerItem<K> current;
                        Visibility visibility;

                        for (int i = 1; i < size; i++) {

                            current = items.get(i);

                            visibility = visibilityProvider.resolveInActiveVisibility(previous.entry, current.entry);

                            // if it's not NULL (not detached), then attach it and apply visibility
                            if (visibility != null) {

                                attach(previous);

                                // should apply after attach (as it sets visibility to VISIBLE automatically)
                                previous.visibility = visibility;

                                // apply actual visibility
                                visibility.apply(previous.view);
                            }

                            previous = current;
                        }
                    }

                    // make last item attached and visible no matter what
                    final ScreenManagerItem<K> item = items.get(size - 1);
                    attach(item);
                }

                // most likely no, but still
                // else, onActivity resume will make it automatically
                if (activityResumed) {
                    active(lastItem());
                }
            }
        }
        return result;
    }

    @NonNull
    @Override
    public <P extends Plugin> P plugin(@NonNull Class<P> plugin) throws IllegalStateException {
        final Plugin p = plugins.get(plugin);
        if (p == null) {
            throw new IllegalStateException(String.format("Requested plugin `%s` is not registered " +
                    "with this ScreenManager instance", plugin.getName()));
        }
        //noinspection unchecked
        return (P) p;
    }

    private static abstract class ReplaceEndAction<K extends Enum<K>> {
        abstract void run(@NonNull ScreenManagerItem<K> previousItem);
    }

    private void listenForActivityEvents() {
        activityLifecycleCallbacks = new ActivityLifecycleCallbacksAdapter() {
            @Override
            public void onActivityResumed(Activity a) {
                if (activity == a) {
                    activityResumed = true;
                    if (items.size() > 0) {
                        final ScreenManagerItem<K> last = lastItem();
                        eventDispatcher.dispatchOnActive(last.screen);
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity a) {
                if (activity == a) {
                    activityResumed = false;
                    if (items.size() > 0) {
                        final ScreenManagerItem<K> last = lastItem();
                        eventDispatcher.dispatchOnInactive(last.screen);
                    }
                }
            }

            @Override
            public void onActivityDestroyed(Activity a) {
                if (activity == a) {
                    dispose();
                }
            }
        };
        activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private void listenForContainerEvents() {
        containerOnAttachStateListener = new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // no op
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                dispose();
            }
        };
        container.addOnAttachStateChangeListener(containerOnAttachStateListener);
    }

    private void dispose() {

        if (activity != null
                && activityLifecycleCallbacks != null) {
            activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
            activity = null;
            activityLifecycleCallbacks = null;
        }

        if (container != null
                && containerOnAttachStateListener != null) {

            // destroy all current items
            final int size = items.size();
            ScreenManagerItem<K> item;
            for (int i = size - 1; i > -1; i--) {
                item = items.get(i);
                inactive(item);
                if (item.view != null) {
                    detach(item, true);
                }
                destroy(item);
            }

            container.removeOnAttachStateChangeListener(containerOnAttachStateListener);
            container = null;
            containerOnAttachStateListener = null;
        }

        historySubscription.unsubscribe();

        items.clear();

        eventDispatcher.clear();

        plugins.clear();
    }

    @NonNull
    private ScreenManagerItem<K> init(@NonNull Entry<K> entry) {

        // create new screen for supplied entry (push operation creates a new screen)
        final Screen<K, ? extends Parcelable> screen = screenProvider.provide(entry.key(), entry.state());

        // create internal representation (holder)
        final ScreenManagerItem<K> item = new ScreenManagerItem<>(entry, screen);

        // keep track of what we have created
        items.add(item);

        // call postConstructor
        eventDispatcher.dispatchInit(screen, this);

        return item;
    }

    private void attach(@NonNull ScreenManagerItem<K> item) {
        attach(item, container.getChildCount());
    }

    private void attach(@NonNull ScreenManagerItem<K> item, int index) {

        // create view for supplied item
        final View view = item.screen.onCreateView(layoutInflater(), container);

        // add newly created view as last view to our container
        container.addView(view, index);

        // store view
        item.view = view;
        item.visibility = Visibility.VISIBLE;

        // dispatch onAttach event
        eventDispatcher.dispatchOnAttach(item.screen, view);
    }

    private void detach(@NonNull ScreenManagerItem<K> item) {
        detach(item, false);
    }

    private void detach(@NonNull ScreenManagerItem<K> item, boolean keepViewInLayout) {

        // detach if view has no visibility
        eventDispatcher.dispatchOnDetach(item.screen, item.view);

        // can be useful for last items (so there is no visual glitch when exiting application)
        if (!keepViewInLayout) {
            // remove from container
            container.removeView(item.view);
        }

        // release view reference
        item.view = null;
    }

    private void inactive(@NonNull ScreenManagerItem<K> item) {
        eventDispatcher.dispatchOnInactive(item.screen);
    }

    private void active(@NonNull ScreenManagerItem<K> item) {
        eventDispatcher.dispatchOnActive(item.screen);
    }

    private void destroy(@NonNull ScreenManagerItem<K> item) {

        // dispatch destroy event
        eventDispatcher.dispatchDestroy(item.screen);

        // clear reference
        items.remove(item);
    }

    @NonNull
    private View viewForPoppedItems(@NonNull List<ScreenManagerItem<K>> items) {

        // if there is only one view -> return it
        // else create a new FrameLayout, add all views to it (add to our container)

        final List<ScreenManagerItem<K>> itemViews = new ArrayList<>(3);
        for (ScreenManagerItem<K> item : items) {
            if (item.view != null) {
                itemViews.add(item);
            }
        }

        final View out;

        if (itemViews.size() == 1) {

            out = itemViews.get(0).view;

        } else {

            final FrameLayout layout = new FrameLayout(activity);

            View view;

            for (ScreenManagerItem<K> item : itemViews) {

                view = item.view;

                // detach, as it's what we are doing:
                detach(item);

                layout.addView(view);
            }

            // add this group to our container
            container.addView(layout);

            out = layout;
        }

        return out;
    }

    @Nullable
    private ScreenManagerItem<K> onPoppedToAppear(@Nullable Entry<K> toAppear, int expectedIndex) {

        final ScreenManagerItem<K> item;

        if (toAppear == null) {

            item = null;

        } else {

            item = items.get(expectedIndex);

            if (item.view == null) {
                attach(item, actualViewIndexForItem(item));
            } else if (View.VISIBLE != item.view.getVisibility()) {
                item.view.setVisibility(View.VISIBLE);
            }

            item.visibility = Visibility.VISIBLE;
        }

        return item;
    }

    private void destroyPopped(@Nullable ScreenManagerItem<K> toAppear, @NonNull View viewForPoppedItems) {

        if (toAppear == null && !detachLastView) {
            return;
        }

        boolean result = items.size() > 0;
        ScreenManagerItem<K> item;

        while (result) {

            item = items.get(items.size() - 1);

            if (item != toAppear) {

                if (item.view != null) {
                    detach(item);
                }

                destroy(item);

                result = items.size() > 0;

            } else {
                result = false;
            }
        }

        // if viewForPoppedItems is still attached -> detach
        if (viewForPoppedItems.getParent() != null) {
            container.removeView(viewForPoppedItems);
        }
    }

    @NonNull
    private LayoutInflater layoutInflater() {
        return layoutInflater != null
                ? layoutInflater
                : activity.getLayoutInflater();
    }
}
