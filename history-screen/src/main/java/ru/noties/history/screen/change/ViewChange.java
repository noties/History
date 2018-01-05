package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class ViewChange implements Change {


    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to
    );

    protected abstract void startAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    );

    protected abstract void cancelAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to
    );

    // final so no erased type info is accessed
    @NonNull
    @Override
    public final ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen from,
            @NonNull Screen to,
            @NonNull Runnable endAction
    ) {
        return apply(reverse, manager.container(), from.view(), to.view(), endAction);
    }

    @NonNull
    protected ChangeCallback apply(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    ) {
        final ChangeCallback changeCallback;

        if (isReady(from, to)) {
            changeCallback = animateNow(reverse, container, from, to, endAction);
        } else {
            changeCallback = animateWhenReady(reverse, container, from, to, endAction);
        }

        return changeCallback;
    }

    protected boolean isReady(@NonNull View from, @NonNull View to) {
        return from.getWidth() > 0 && to.getWidth() > 0;
    }

    @NonNull
    protected ChangeCallback animateNow(
            final boolean reverse,
            @NonNull final ViewGroup container,
            @NonNull final View from,
            @NonNull final View to,
            @NonNull final Runnable endAction
    ) {

        applyStartValues(reverse, container, from, to);

        startAnimation(reverse, container, from, to, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelAnimation(reverse, container, from, to);
                endAction.run();
            }
        };
    }

    @NonNull
    protected ChangeCallback animateWhenReady(
            final boolean reverse,
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
                    applyStartValues(reverse, container, from, to);
                    startAnimation(reverse, container, from, to, endAction);
                }
                return true;
            }
        };

        addOnPreDrawListener(from, listener);
        addOnPreDrawListener(to, listener);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                if (started.mark) {
                    cancelAnimation(reverse, container, from, to);
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
