package ru.noties.history.screen;

import android.support.annotation.NonNull;

public interface ScreenLifecycle {

    // we earliest we can request a lifecycle event triggered is in INIT, so it cannot be here
    enum Event {
        ATTACH,
        DETACH,
        ACTIVE,
        INACTIVE,
        DESTROY
    }

    interface Action {
        void apply();
    }

    void on(@NonNull Event event, @NonNull Action action);
}
