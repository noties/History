package ru.noties.screen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import ru.noties.screen.transition.TransitionLock;

/**
 * Simple implementation of FrameLayout that disables all touch events whilst change between
 * multiple screens occur. Should be used as root container for {@link ScreenManager}
 *
 * @see TransitionLock
 */
public class ScreenLayout extends FrameLayout implements TransitionLock {

    private boolean locked;

    public ScreenLayout(Context context) {
        super(context);
    }

    public ScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void lock() {
        this.locked = true;
    }

    @Override
    public void unlock() {
        this.locked = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return locked || super.dispatchTouchEvent(ev);
    }
}
