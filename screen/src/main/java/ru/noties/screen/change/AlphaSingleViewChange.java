package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AlphaSingleViewChange extends SingleViewChange {

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> to(long duration) {
        //noinspection unchecked
        return new AlphaSingleViewChange(duration, .0F, 1.F);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> from(long duration) {
        //noinspection unchecked
        return new AlphaSingleViewChange(duration, 1.F, .0F);
    }

    private final long duration;
    private final float start;
    private final float end;

    public AlphaSingleViewChange(long duration, float start, float end) {
        this.duration = duration;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        view.setAlpha(reverse ? end : start);
    }

    @Override
    protected void startAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view, @NonNull Runnable endAction) {
        view.animate()
                .alpha(reverse ? start : end)
                .setDuration(duration)
                .withEndAction(endAction)
                .start();
    }

    @Override
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        view.clearAnimation();
    }
}
