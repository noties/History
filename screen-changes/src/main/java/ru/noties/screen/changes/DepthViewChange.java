package ru.noties.screen.changes;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.screen.change.Change;
import ru.noties.tumbleweed.BaseTween;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.TimelineDef;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.TweenCallback;
import ru.noties.tumbleweed.android.types.Alpha;
import ru.noties.tumbleweed.android.types.Scale;
import ru.noties.tumbleweed.android.types.Translation;

public class DepthViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new DepthViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new DepthViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new DepthViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new DepthViewChange(duration, false, false);
    }

    private static final float MIN_SCALE = .75F;

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private DepthViewChange(float duration, boolean isHorizontal, boolean isStart) {
        this.duration = duration / 1000.F;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            final float toX;

            if (isStart) {
                toX = reverse ? .0F : -to.getWidth();
            } else {
                toX = reverse ? .0F : to.getWidth();
            }

            from.setTranslationX(.0F);
            to.setTranslationX(toX);

        } else {

            final float toY;

            if (isStart) {
                toY = reverse ? .0F : -to.getHeight();
            } else {
                toY = reverse ? .0F : to.getHeight();
            }

            from.setTranslationY(.0F);
            to.setTranslationY(toY);
        }

        final float fromScale = reverse ? MIN_SCALE : 1.F;
        final float fromAlpha = reverse ? .0F : 1.F;

        from.setScaleX(fromScale);
        from.setScaleY(fromScale);

        from.setAlpha(fromAlpha);
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createParallel();

        if (isHorizontal) {

            final float toX;

            if (isStart) {
                toX = reverse ? -to.getWidth() : .0F;
            } else {
                toX = reverse ? to.getWidth() : .0F;
            }

            timeline
                    .push(Tween.to(to, Translation.X, duration).target(toX));
        } else {

            final float toY;

            if (isStart) {
                toY = reverse ? -to.getHeight() : .0F;
            } else {
                toY = reverse ? to.getHeight() : .0F;
            }

            timeline
                    .push(Tween.to(to, Translation.Y, duration).target(toY));

        }

        final float fromScale = reverse ? 1.F : MIN_SCALE;

        timeline
                .push(Tween.to(from, Scale.XY, duration).target(fromScale, fromScale))
                .push(Tween.to(from, Alpha.VIEW, duration).target(reverse ? 1.F : .0F));

        timeline
                .callback(TweenCallback.END, new TweenCallback() {
                    @Override
                    public void onEvent(int type, @NonNull BaseTween source) {
                        endAction.run();
                    }
                })
                .start(tweenManager(container));
    }

    @Override
    protected void cancelChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        tweenManager(container).killAll();
    }
}
