package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    public CombinedChangeCallback from(@Nullable ChangeCallback changeCallback) {
        this.from = changeCallback;
        return this;
    }

    @NonNull
    public CombinedChangeCallback to(@Nullable ChangeCallback changeCallback) {
        this.to = changeCallback;
        return this;
    }
}
