package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class Change<K extends Enum<K>> {

    @NonNull
    public ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {

        final ChangeCallback changeCallback;

        if (isReady(from)
                && isReady(to)) {
            changeCallback = applyNow(reverse, manager, from, to, endAction);
        } else {
            changeCallback = applyWhenReady(reverse, manager, from, to, endAction);
        }

        return changeCallback;
    }

    @NonNull
    protected ChangeCallback applyNow(
            final boolean reverse,
            @NonNull final ScreenManager<K> manager,
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to,
            @NonNull final Runnable endAction
    ) {

        applyStartValues(reverse, manager, from, to);

        executeChange(reverse, manager, from, to, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelChange(reverse, manager, from, to);
                endAction.run();
            }
        };
    }

    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );

    protected abstract void executeChange(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    protected abstract void cancelChange(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );

    @NonNull
    protected ChangeCallback applyWhenReady(
            final boolean reverse,
            @NonNull final ScreenManager<K> manager,
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to,
            @NonNull final Runnable endAction
    ) {

        final Holder holder = new Holder();

        final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (isReady(from) && isReady(to)) {
                    removeOnPreDrawListener(from, this);
                    removeOnPreDrawListener(to, this);
                    holder.callback = applyNow(reverse, manager, from, to, endAction);
                }

                // we won't be blocking drawing calls
                return true;
            }
        };

        addOnPreDrawListener(from, listener);
        addOnPreDrawListener(to, listener);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                if (holder.callback != null) {
                    holder.callback.cancel();
                } else {
                    removeOnPreDrawListener(from, listener);
                    removeOnPreDrawListener(to, listener);
                    endAction.run();
                }
            }
        };
    }

    protected boolean isReady(@NonNull Screen<K, ? extends Parcelable> screen) {
        return screen.view().getWidth() > 0;
    }

    protected static void addOnPreDrawListener(@NonNull Screen<?, ?> screen, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        final View view = screen.view();
        view.getViewTreeObserver().addOnPreDrawListener(listener);
        view.invalidate();
    }

    protected static void removeOnPreDrawListener(@NonNull Screen<?, ?> screen, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        screen.view().getViewTreeObserver().removeOnPreDrawListener(listener);
    }

    private static class Holder {
        ChangeCallback callback;
    }
}
