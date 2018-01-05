package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

@Deprecated
public abstract class AbsTransition implements Transition {


    protected abstract void applyStartValues(@NonNull View from, @NonNull View to);

    protected abstract void runTransition(@NonNull View from, @NonNull View to, @NonNull Runnable endAction);

    protected abstract void cancelTransition(@NonNull View from, @NonNull View to);


    @Override
    @NonNull
    public Callback animate(@NonNull View from, @NonNull View to, @NonNull Runnable endAction) {
        final Callback callback;
        if (isReady(from, to)) {
            callback = animateOnReady(from, to, endAction);
        } else {
            callback = animateWhenReady(from, to, endAction);
        }
        return callback;
    }

    protected boolean isReady(@NonNull View view) {
        return view.getWidth() > 0;
    }

    protected boolean isReady(@NonNull View from, @NonNull View to) {
        return isReady(from) && isReady(to);
    }

    @NonNull
    protected Callback animateWhenReady(@NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                final boolean result = isReady(from, to);

                if (result) {

                    unregister(from, this);
                    unregister(to, this);

                    applyStartValues(from, to);

                    runTransition(from, to, endAction);
                }

                return result;
            }
        };


        if (!isReady(from)) {
            register(from, listener);
            from.invalidate();
        }

        if (!isReady(to)) {
            register(to, listener);
            to.invalidate();
        }

        return new Callback() {
            @Override
            public void cancel() {
                unregister(from, listener);
                unregister(to, listener);
                cancelTransition(from, to);
                endAction.run();
            }
        };
    }

    @NonNull
    protected Callback animateOnReady(@NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {
        applyStartValues(from, to);
        runTransition(from, to, endAction);
        return new Callback() {
            @Override
            public void cancel() {
                cancelTransition(from, to);
                endAction.run();
            }
        };
    }

    protected static void register(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        view.getViewTreeObserver().addOnPreDrawListener(listener);
    }

    protected static void unregister(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        view.getViewTreeObserver().removeOnPreDrawListener(listener);
    }
}
