package ru.noties.history.screen.change;

import android.support.annotation.NonNull;

public abstract class ChangeCallbackNoOp {

    @NonNull
    public static ChangeCallback noOp(@NonNull Runnable endAction) {
        endAction.run();
        return NO_OP;
    }

    private static final ChangeCallback NO_OP = new ChangeCallback() {
        @Override
        public void cancel() {

        }
    };

    private ChangeCallbackNoOp() {
    }
}
