package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class SwitchEngineCallback {

    @Nullable
    public static SwitchEngineCallback noOp(@NonNull Runnable endAction) {
        endAction.run();
        return null;
    }

    public abstract void cancel();

}
