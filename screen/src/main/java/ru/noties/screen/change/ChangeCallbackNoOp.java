package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public abstract class ChangeCallbackNoOp {

    @Nullable
    public static ChangeCallback noOp(@NonNull Runnable endAction) {
        endAction.run();
        return null;
    }

    private ChangeCallbackNoOp() {
    }
}
