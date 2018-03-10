package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Simple alpha transition (cross-fade)
 *
 * @see #create(long)
 * @see #create(long, Class)
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AlphaTransition extends ViewTransition {

    /**
     * @param duration in milliseconds
     * @return {@link ScreenTransition}
     */
    @NonNull
    private static <K extends Enum<K>> ScreenTransition<K> create(long duration) {
        //noinspection unchecked
        return new AlphaTransition(duration);
    }

    /**
     * @param duration in milliseconds
     * @param type     class of enum key for type information
     * @return {@link ScreenTransition}
     */
    @NonNull
    private static <K extends Enum<K>> ScreenTransition<K> create(long duration, @NonNull Class<K> type) {
        //noinspection unchecked
        return new AlphaTransition(duration);
    }

    private final long duration;

    protected AlphaTransition(long duration) {
        this.duration = duration;
    }

    @Nullable
    @Override
    protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        from.clearAnimation();
        to.clearAnimation();

        from.setAlpha(reverse ? .0F : 1.F);
        to.setAlpha(reverse ? 1.F : .0F);

        from.animate()
                .alpha(reverse ? 1.F : .0F)
                .setDuration(duration)
                .start();

        to.animate()
                .alpha(reverse ? .0F : 1.F)
                .setDuration(duration)
                .withEndAction(endAction)
                .start();

        return new TransitionCallback() {
            @Override
            public void cancel() {

                from.clearAnimation();
                to.clearAnimation();

                endAction.run();
            }
        };
    }
}
