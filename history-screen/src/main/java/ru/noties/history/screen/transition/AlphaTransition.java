package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.view.View;

@Deprecated
public class AlphaTransition extends AbsTransition {

    private final long duration;

    public AlphaTransition(long duration) {
        this.duration = duration;
    }

    @Override
    protected void applyStartValues(@NonNull View from, @NonNull View to) {
        from.setAlpha(1.F);
        to.setAlpha(.0F);
    }

    @Override
    protected void runTransition(@NonNull View from, @NonNull View to, @NonNull Runnable endAction) {
        from.animate()
                .alpha(.0F)
                .setDuration(duration)
                .start();

        to.animate()
                .alpha(1.F)
                .setDuration(duration)
                .withEndAction(endAction)
                .start();
    }

    @Override
    protected void cancelTransition(@NonNull View from, @NonNull View to) {
        from.clearAnimation();
        to.clearAnimation();
    }
}
