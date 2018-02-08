package ru.noties.screen.transition;

import ru.noties.screen.ScreenLayout;
import ru.noties.screen.ScreenManagerBuilder;

/**
 * Simple interface to perform an action during layout change (transition) occurs. For example,
 * disabling all touch events
 *
 * @see ScreenLayout
 * @see ScreenManagerBuilder#transitionLock(TransitionLock)
 */
public interface TransitionLock {

    /**
     * Will be called before change
     */
    void lock();

    /**
     * Will be called after change is complete
     */
    void unlock();
}
