package ru.noties.history.screen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class ScreenLayout extends FrameLayout implements ChangeLock {

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
