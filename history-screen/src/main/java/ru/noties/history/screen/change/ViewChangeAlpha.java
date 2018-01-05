package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ViewChangeAlpha extends ViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> create(long duration) {
        //noinspection unchecked
        return new ViewChangeAlpha(duration);
    }

    private final long duration;

    public ViewChangeAlpha(long duration) {
        this.duration = duration;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        from.setAlpha(1.F);
        to.setAlpha(.0F);
    }

    @Override
    protected void startAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

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
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        from.clearAnimation();
        to.clearAnimation();
    }
}
