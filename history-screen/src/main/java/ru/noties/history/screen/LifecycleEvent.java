package ru.noties.history.screen;

/**
 * Simple holder of available lifecycle events to be listen to.
 * As the INIT is the earliest when subscription can occur it\'s not included in this class.
 *
 * @see ScreenLifecycle#on(LifecycleEvent, ScreenLifecycle.Action)
 */
public enum LifecycleEvent {

    /**
     * @see Screen#onAttach(android.view.View)
     */
    ATTACH,

    /**
     * @see Screen#onDetach(android.view.View)
     */
    DETACH,

    /**
     * @see Screen#onActive()
     */
    ACTIVE,

    /**
     * @see Screen#onInactive()
     */
    INACTIVE,

    /**
     * @see Screen#destroy()
     */
    DESTROY
}
