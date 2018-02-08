package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewParent;

import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.ViewTweenManager;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class TweenViewTransition extends ViewTransition {

    @NonNull
    public static TweenManager tweenManager(@NonNull View container) {
        return ViewTweenManager.get(R.id.screen_tween_manager, container);
    }

    @NonNull
    public static TweenManager tweenManagerParent(@NonNull View view) {
        final View parent = parent(view);
        return ViewTweenManager.get(R.id.screen_tween_manager, parent);
    }

    public static void kill(@NonNull TweenManager manager, @NonNull View from, @NonNull View to) {
        manager.killTarget(from);
        manager.killTarget(to);
    }

    @NonNull
    public static View parent(@NonNull View view) {
        final ViewParent parent = view.getParent();
        if (parent == null
                || !(parent instanceof View)) {
            throw new IllegalStateException("Specified view has other than View parent, view: " + view);
        }
        return (View) parent;
    }

    public static float toSeconds(long millis) {
        return millis / 1000.F;
    }
}
