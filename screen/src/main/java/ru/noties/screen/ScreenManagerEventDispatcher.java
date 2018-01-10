package ru.noties.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import ru.noties.history.Subscription;
import ru.noties.history.SubscriptionImpl;
import ru.noties.listeners.Listeners;

class ScreenManagerEventDispatcher<K extends Enum<K>> {

    private final Listeners<ScreenLifecycleCallbacks<K>> callbacks = Listeners.create(3);

    private final Map<Screen<K, ? extends Parcelable>, ScreenLifecycleImpl<K>> lifecycle
            = new HashMap<>(3);

    @NonNull
    Subscription callbacks(@NonNull ScreenLifecycleCallbacks<K> screenLifecycleCallbacks) {
        return new SubscriptionImpl<>(callbacks, screenLifecycleCallbacks);
    }

    @NonNull
    ScreenLifecycle lifecycle(@NonNull Screen<K, ? extends Parcelable> screen) {
        ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle == null) {
            screenLifecycle = new ScreenLifecycleImpl<>(screen);
            lifecycle.put(screen, screenLifecycle);
        }
        return screenLifecycle;
    }

    void clear() {
        callbacks.clear();
        lifecycle.clear();
    }

    void dispatchInit(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager) {

        // general callbacks will be called before actual event
        // this way we would be able for example to inject dependencies
        // and at the moment screen will receive this event all dependencies will be
        // injected and ready to be used
        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.init(screen, manager);
        }

        screen.init(manager);

        // now we can dispatch event to screen local observers
        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.init(screen, manager);
        }
    }

    void dispatchDestroy(@NonNull Screen<K, ? extends Parcelable> screen) {

        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.destroy(screen);
        }

        screen.destroy();

        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.destroy(screen);

            // additionally clear
            lifecycle.remove(screen);
        }
    }

    void dispatchOnAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {

        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.onAttach(screen, view);
        }

        screen.onAttach(view);

        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.onAttach(screen, view);
        }
    }

    void dispatchOnDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {

        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.onDetach(screen, view);
        }

        screen.onDetach(view);

        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.onDetach(screen, view);
        }
    }

    void dispatchOnActive(@NonNull Screen<K, ? extends Parcelable> screen) {

        if (screen.isActive()) {
            return;
        }

        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.onActive(screen);
        }

        screen.onActive();

        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.onActive(screen);
        }
    }

    void dispatchOnInactive(@NonNull Screen<K, ? extends Parcelable> screen) {

        if (!screen.isActive()) {
            return;
        }

        for (ScreenLifecycleCallbacks<K> screenLifecycleCallbacks : callbacks.begin()) {
            screenLifecycleCallbacks.onInactive(screen);
        }

        screen.onInactive();

        final ScreenLifecycleImpl<K> screenLifecycle = lifecycle.get(screen);
        if (screenLifecycle != null) {
            screenLifecycle.onInactive(screen);
        }
    }
}
