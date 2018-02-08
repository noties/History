package ru.noties.screen.transition;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import ru.noties.screen.Screen;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ScreenTransition<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> noOp() {
        //noinspection unchecked
        return NO_OP;
    }

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> noOp(@NonNull Class<K> type) {
        //noinspection unchecked
        return (ScreenTransition<K>) NO_OP;
    }


    @Nullable
    public TransitionCallback apply(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {

        final TransitionCallback callback;

        if (isReady(from) && isReady(to)) {
            callback = applyNow(reverse, from, to, endAction);
        } else {
            callback = applyWhenReady(reverse, from, to, endAction);
        }

        return callback;
    }

    @Nullable
    protected abstract TransitionCallback applyNow(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @Nullable
    protected TransitionCallback applyWhenReady(
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

        return new TransitionCallback() {
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

    private static final ScreenTransition NO_OP = new ScreenTransition() {
        @Nullable
        @Override
        public final TransitionCallback apply(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
            return TransitionCallback.noOp(endAction);
        }

        @Override
        protected TransitionCallback applyNow(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
            throw new RuntimeException();
        }
    };

    private static class Holder {
        TransitionCallback callback;
    }
}
