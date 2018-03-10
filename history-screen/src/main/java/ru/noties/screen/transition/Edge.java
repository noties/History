package ru.noties.screen.transition;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Simple indicator of an edge from which to start transition.
 * <p>
 * For RTL friendly transitions consider using {@link #start(Context)} and {@link #end(Context)}
 * factory methods
 */
public enum Edge {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    @NonNull
    public static Edge start(@NonNull Context context) {
        return isRtl(context)
                ? RIGHT
                : LEFT;
    }

    @NonNull
    public static Edge end(@NonNull Context context) {
        return isRtl(context)
                ? LEFT
                : RIGHT;
    }

    private static boolean isRtl(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= 17
                && View.LAYOUT_DIRECTION_RTL == context.getResources().getConfiguration().getLayoutDirection();
    }
}
