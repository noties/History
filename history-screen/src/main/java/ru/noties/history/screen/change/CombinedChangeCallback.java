package ru.noties.history.screen.change;

import android.support.annotation.NonNull;

public class CombinedChangeCallback implements ChangeCallback {

    private ChangeCallback from;
    private ChangeCallback to;

    @Override
    public void cancel() {

        if (from != null) {
            from.cancel();
        }

        if (to != null) {
            to.cancel();
        }
    }

    @NonNull
    public CombinedChangeCallback from(@NonNull ChangeCallback changeCallback) {
        this.from = changeCallback;
        return this;
    }

    @NonNull
    public CombinedChangeCallback to(@NonNull ChangeCallback changeCallback) {
        this.to = changeCallback;
        return this;
    }
}
