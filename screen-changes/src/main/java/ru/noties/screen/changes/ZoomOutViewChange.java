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

public class ZoomOutViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new ZoomOutViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new ZoomOutViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new ZoomOutViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new ZoomOutViewChange(duration, false, false);
    }

    private static final float MIN_SCALE = .85F;
    private static final float MIN_ALPHA = .5F;

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private ZoomOutViewChange(float duration, boolean isHorizontal, boolean isStart) {
        // we have 3 steps
        this.duration = (duration / 1000.F) / 3;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? from.getWidth() : .0F;
                toX = reverse ? .0F : -to.getWidth();
            } else {
                fromX = reverse ? -from.getWidth() : .0F;
                toX = reverse ? .0F : to.getWidth();
            }

            from.setTranslationX(fromX);
            to.setTranslationX(toX);
        } else {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? from.getHeight() : .0F;
                toY = reverse ? .0F : -to.getHeight();
            } else {
                fromY = reverse ? -from.getHeight() : .0F;
                toY = reverse ? .0F : to.getHeight();
            }

            from.setTranslationY(fromY);
            to.setTranslationY(toY);
        }

        final float fromScale = reverse ? MIN_SCALE : 1.F;
        final float fromAlpha = reverse ? MIN_ALPHA : 1.F;

        final float toScale = reverse ? 1.F : MIN_SCALE;
        final float toAlpha = reverse ? 1.F : MIN_ALPHA;

        from.setScaleX(fromScale);
        from.setScaleY(fromScale);

        from.setAlpha(fromAlpha);

        to.setScaleX(toScale);
        to.setScaleY(toScale);

        to.setAlpha(toAlpha);
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createSequence();

        final View start = reverse ? to : from;

        timeline
                .beginParallel()
                .push(Tween.to(start, Scale.XY, duration).target(MIN_SCALE, MIN_SCALE))
                .push(Tween.to(start, Alpha.VIEW, duration).target(MIN_ALPHA))
                .end();

        if (isHorizontal) {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? .0F : from.getWidth();
                toX = reverse ? -to.getWidth() : .0F;
            } else {
                fromX = reverse ? .0F : -from.getWidth();
                toX = reverse ? to.getWidth() : .0F;
            }

            timeline
                    .beginParallel()
                    .push(Tween.to(from, Translation.X, duration).target(fromX))
                    .push(Tween.to(to, Translation.X, duration).target(toX))
                    .end();
        } else {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? .0F : from.getHeight();
                toY = reverse ? -to.getHeight() : .0F;
            } else {
                fromY = reverse ? .0F : -from.getHeight();
                toY = reverse ? to.getHeight() : .0F;
            }

            timeline
                    .beginParallel()
                    .push(Tween.to(from, Translation.Y, duration).target(fromY))
                    .push(Tween.to(to, Translation.Y, duration).target(toY))
                    .end();
        }

        final View end = reverse ? from : to;

        timeline
                .beginParallel()
                .push(Tween.to(end, Scale.XY, duration).target(1.F, 1.F))
                .push(Tween.to(end, Alpha.VIEW, duration).target(1.F))
                .end();

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
