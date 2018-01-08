package ru.noties.history.screen;

import android.support.annotation.NonNull;

/**
 * @see Screen#lifecycle()
 * @see ScreenManager#screenLifecycle(Screen)
 */
public interface ScreenLifecycle {

    interface Action {
        void apply();
    }

    /**
     * Please note that these actions are one-shot, after event is triggered {@link Action} will
     * be automatically disposed
     *
     * @param event  {@link LifecycleEvent} when to be notified
     * @param action {@link Action} that will be triggered when specified `event` occurs
     */
    void on(@NonNull LifecycleEvent event, @NonNull Action action);
}
