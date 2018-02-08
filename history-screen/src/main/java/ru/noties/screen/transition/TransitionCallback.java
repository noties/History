package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class TransitionCallback {

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static TransitionCallback noOp(@NonNull Runnable endAction) {
        endAction.run();
        return null;
    }

    public abstract void cancel();
}
