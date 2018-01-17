package ru.noties.screen.transit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public class ValueAnimatorEngine<K extends Enum<K>> extends SwitchEngine<K> {

    public interface Config {
        void apply(boolean reverse, @NonNull ValueAnimator animator);
    }

    @NonNull
    public static <K extends Enum<K>> SwitchEngine<K> create(
            @NonNull ScreenSwitch<K> screenSwitch,
            long duration
    ) {
        return new ValueAnimatorEngine<>(screenSwitch, CONFIG_NO_OP, duration);
    }

    // note, that config must set duration
    @NonNull
    public static <K extends Enum<K>> SwitchEngine<K> create(
            @NonNull ScreenSwitch<K> screenSwitch,
            @NonNull Config config
    ) {
        return new ValueAnimatorEngine<>(screenSwitch, config, 0L);
    }

    @NonNull
    public static <K extends Enum<K>> SwitchEngine<K> create(
            @NonNull ScreenSwitch<K> screenSwitch,
            long duration,
            @NonNull Config config
    ) {
        return new ValueAnimatorEngine<>(screenSwitch, config, duration);
    }

    private final Config config;

    private final long duration;

    // todo: should we maybe cache the ValueAnimator?...

    protected ValueAnimatorEngine(@NonNull ScreenSwitch<K> screenSwitch, @NonNull Config config, long duration) {
        super(screenSwitch);
        this.config = config;
        this.duration = duration;
    }

    @Nullable
    @Override
    protected SwitchEngineCallback applyNow(
            final boolean reverse,
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to,
            @NonNull final Runnable endAction
    ) {

        final ValueAnimator animator = ValueAnimator.ofFloat(.0F, 1.F);
        animator.setDuration(duration);

        config.apply(reverse, animator);

        // apply initial values
        screenSwitch.apply(reverse ? 1.F : .0F, from, to);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float fraction = reverse
                        ? 1.F - animation.getAnimatedFraction()
                        : animation.getAnimatedFraction();
                screenSwitch.apply(fraction, from, to);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                endAction.run();
            }
        });

        animator.start();

        return new SwitchEngineCallback() {
            @Override
            public void cancel() {
                if (animator.isRunning()) {
                    animator.cancel();
                }
                endAction.run();
            }
        };
    }

    private static final Config CONFIG_NO_OP = new Config() {
        @Override
        public void apply(boolean reverse, @NonNull ValueAnimator animator) {

        }
    };
}
