package ru.noties.history.screen;

import android.support.annotation.NonNull;

public interface ScreenLifecycle {

    interface Action {
        void apply();
    }

    void on(@NonNull LifecycleEvent event, @NonNull Action action);
}
