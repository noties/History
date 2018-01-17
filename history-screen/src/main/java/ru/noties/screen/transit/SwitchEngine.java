package ru.noties.screen.transit;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import ru.noties.screen.Screen;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class SwitchEngine<K extends Enum<K>> {

    protected final ScreenSwitch<K> screenSwitch;

    protected SwitchEngine(@NonNull ScreenSwitch<K> screenSwitch) {
        this.screenSwitch = screenSwitch;
    }

    @Nullable
    public SwitchEngineCallback apply(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {

        final SwitchEngineCallback callback;

        if (isReady(from) && isReady(to)) {
            callback = applyNow(reverse, from, to, endAction);
        } else {
            callback = applyWhenReady(reverse, from, to, endAction);
        }

        return callback;
    }

    // when restoring screen state this method will be called for visible screens
    public void forceEndValues(
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to
    ) {

        if (isReady(from) && isReady(to)) {

            screenSwitch.apply(1.F, from, to);

        } else {

            final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    if (isReady(from) && isReady(to)) {
                        removeOnPreDrawListener(from, this);
                        removeOnPreDrawListener(to, this);
                        screenSwitch.apply(1.F, from, to);
                        return true;
                    }

                    return false;
                }
            };

            addOnPreDrawListener(from, listener);
            addOnPreDrawListener(to, listener);
        }
    }

    @Nullable
    protected abstract SwitchEngineCallback applyNow(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @Nullable
    protected SwitchEngineCallback applyWhenReady(
            final boolean reverse,
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
                    holder.callback = applyNow(reverse, from, to, endAction);
                }

                // do not block draw call
                return true;
            }
        };

        addOnPreDrawListener(from, listener);
        addOnPreDrawListener(to, listener);

        return new SwitchEngineCallback() {
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

    protected static boolean isReady(@NonNull Screen<?, ? extends Parcelable> screen) {
        return screen.view().getWidth() > 0;
    }

    protected static void addOnPreDrawListener(@NonNull Screen<?, ?> screen, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        final View view = screen.view();
        view.getViewTreeObserver().addOnPreDrawListener(listener);
        view.invalidate();
    }

    protected static void removeOnPreDrawListener(@NonNull Screen<?, ?> screen, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        final ViewTreeObserver viewTreeObserver = screen.view().getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(listener);
        }
    }

    private static class Holder {
        SwitchEngineCallback callback;
    }
}
