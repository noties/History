package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

@SuppressWarnings("WeakerAccess")
public abstract class AbsChange implements Change {

    // todo: actually it doesn't make sense, we allow inspecting Screen, but providing only View for animation
    // must be changed

    protected abstract void applyStartValues(@NonNull ViewGroup container, @NonNull View from, @NonNull View to);

    protected abstract void startAnimation(@NonNull ViewGroup container, @NonNull View from, @NonNull View to, @NonNull Runnable endAction);

    protected abstract void cancelAnimation(@NonNull ViewGroup container, @NonNull View from, @NonNull View to);


    @NonNull
    @Override
    public ChangeCallback animate(
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    ) {

        final ChangeCallback changeCallback;

        if (isReady(from, to)) {
            changeCallback = animateNow(container, from, to, endAction);
        } else {
            changeCallback = animateWhenReady(container, from, to, endAction);
        }

        return changeCallback;
    }

    protected boolean isReady(@NonNull View from, @NonNull View to) {
        return from.getWidth() > 0 && to.getWidth() > 0;
    }

    @NonNull
    protected ChangeCallback animateNow(
            @NonNull final ViewGroup container,
            @NonNull final View from,
            @NonNull final View to,
            @NonNull final Runnable endAction
    ) {

        applyStartValues(container, from, to);

        startAnimation(container, from, to, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelAnimation(container, from, to);
                endAction.run();
            }
        };
    }

    @NonNull
    protected ChangeCallback animateWhenReady(
            @NonNull final ViewGroup container,
            @NonNull final View from,
            @NonNull final View to,
            @NonNull final Runnable endAction
    ) {

        final Started started = new Started();

        final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isReady(from, to)) {
                    started.mark = true;
                    removeOnPreDrawListener(from, this);
                    removeOnPreDrawListener(to, this);
                    applyStartValues(container, from, to);
                    startAnimation(container, from, to, endAction);
                    return true;
                }
                return false;
            }
        };

        addOnPreDrawListener(from, listener);
        addOnPreDrawListener(to, listener);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                if (started.mark) {
                    cancelAnimation(container, from, to);
                } else {
                    removeOnPreDrawListener(from, listener);
                    removeOnPreDrawListener(to, listener);
                }
                endAction.run();
            }
        };
    }

    private static void addOnPreDrawListener(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        view.getViewTreeObserver().addOnPreDrawListener(listener);
        view.invalidate();
    }

    private static void removeOnPreDrawListener(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
        view.getViewTreeObserver().removeOnPreDrawListener(listener);
    }

    private static class Started {
        boolean mark;
    }
}
