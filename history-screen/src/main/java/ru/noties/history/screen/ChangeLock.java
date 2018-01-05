package ru.noties.history.screen;

/**
 * Simple interface to perform an action during layout change (transition) occurs. For example,
 * disabling all touch events
 *
 * @see ScreenLayout
 */
public interface ChangeLock {

    /**
     * Will be called before change
     */
    void lock();

    /**
     * Will be called after change is complete
     */
    void unlock();
}
