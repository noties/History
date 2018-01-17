package ru.noties.screen;

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
     * be automatically disposed. Also note that unlike {@link ScreenLifecycleCallbacks} these
     * actions applied <strong>after</strong> actual event on a {@link Screen}, so for example:
     * {@code screen.lifecycle().on(LifecycleEvent.DESTROY, () -> screen.isDestroyed() here is true)}
     *
     * @param event  {@link LifecycleEvent} when to be notified
     * @param action {@link Action} that will be triggered when specified `event` occurs
     */
    void on(@NonNull LifecycleEvent event, @NonNull Action action);
}
