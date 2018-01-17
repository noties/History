package ru.noties.screen.transit;

import android.support.annotation.NonNull;

public enum Edge {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    public static boolean isHorizontal(@NonNull Edge edge) {
        return Edge.LEFT == edge
                || Edge.RIGHT == edge;
    }
}
