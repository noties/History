package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SingleViewChangeAlpha extends SingleViewChange {

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> create(long duration) {
        //noinspection unchecked
        return new SingleViewChangeAlpha(duration);
    }

    private final long duration;

    public SingleViewChangeAlpha(long duration) {
        this.duration = duration;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        view.setAlpha(reverse ? 1.F : .0F);
    }

    @Override
    protected void startAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view, @NonNull Runnable endAction) {
        view.animate()
                .alpha(reverse ? .0F : 1.F)
                .setDuration(duration)
                .withEndAction(endAction)
                .start();
    }

    @Override
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        view.clearAnimation();
    }
}
