package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class SingleViewChange implements SingleChange {

    // todo: beware that views are animated separately, so some visual glitches might occur

    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view
    );

    protected abstract void startAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view,
            @NonNull Runnable endAction
    );

    protected abstract void cancelAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view
    );


    @NonNull
    @Override
    public final ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen screen,
            @NonNull Runnable endAction
    ) {
        return apply(reverse, manager.container(), screen.view(), endAction);
    }

    @NonNull
    protected ChangeCallback apply(
            final boolean reverse,
            @NonNull final ViewGroup container,
            @NonNull final View view,
            @NonNull final Runnable endAction
    ) {

        applyStartValues(reverse, container, view);

        startAnimation(reverse, container, view, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelAnimation(reverse, container, view);
                endAction.run();
            }
        };
    }
//
//    protected boolean isReady(@NonNull View view) {
//        return view.getWidth() > 0;
//    }
//
//    @NonNull
//    protected ChangeCallback animateNow(
//            final boolean reverse,
//            @NonNull final ViewGroup container,
//            @NonNull final View view,
//            @NonNull final Runnable endAction
//    ) {
//
//    }
//
//    @NonNull
//    protected ChangeCallback animateWhenReady(
//            final boolean reverse,
//            @NonNull final ViewGroup container,
//            @NonNull final View view,
//            @NonNull final Runnable endAction
//    ) {
//
//        final Started started = new Started();
//
//        final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                if (isReady(view)) {
//                    started.mark = true;
//                    removeOnPreDrawListener(view, this);
//                    applyStartValues(reverse, container, view);
//                    startAnimation(reverse, container, view, endAction);
//                    return true;
//                }
//                return false;
//            }
//        };
//
//        addOnPreDrawListener(view, listener);
//
//        return new ChangeCallback() {
//            @Override
//            public void cancel() {
//                if (started.mark) {
//                    cancelAnimation(reverse, container, view);
//                } else {
//                    removeOnPreDrawListener(view, listener);
//                }
//                endAction.run();
//            }
//        };
//    }
//
//    private static void addOnPreDrawListener(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
//        view.getViewTreeObserver().addOnPreDrawListener(listener);
//        view.invalidate();
//    }
//
//    private static void removeOnPreDrawListener(@NonNull View view, @NonNull ViewTreeObserver.OnPreDrawListener listener) {
//        view.getViewTreeObserver().removeOnPreDrawListener(listener);
//    }
//
//    private static class Started {
//        boolean mark;
//    }
}
