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
import ru.noties.tumbleweed.android.types.Translation;

public class ParallaxViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new ParallaxViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new ParallaxViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new ParallaxViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new ParallaxViewChange(duration, false, false);
    }

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private ParallaxViewChange(float duration, boolean isHorizontal, boolean isStart) {
        this.duration = duration / 1000.F;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? from.getWidth() / 2 : 0;
                toX = reverse ? .0F : -to.getWidth();
            } else {
                fromX = reverse ? -from.getWidth() / 2 : .0F;
                toX = reverse ? .0F : to.getWidth();
            }

            from.setTranslationX(fromX);
            to.setTranslationX(toX);
        } else {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? from.getHeight() / 2 : .0F;
                toY = reverse ? .0F : -to.getHeight();
            } else {
                fromY = reverse ? -from.getHeight() / 2 : .0F;
                toY = reverse ? .0F : to.getHeight();
            }

            from.setTranslationY(fromY);
            to.setTranslationY(toY);
        }
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createParallel();

        if (isHorizontal) {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? .0F : from.getWidth() / 2;
                toX = reverse ? -to.getWidth() : .0F;
            } else {
                fromX = reverse ? .0F : -from.getWidth() / 2;
                toX = reverse ? to.getWidth() : .0F;
            }

            timeline
                    .push(Tween.to(from, Translation.X, duration).target(fromX))
                    .push(Tween.to(to, Translation.X, duration).target(toX));

        } else {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? .0F : from.getHeight() / 2;
                toY = reverse ? -to.getHeight() : .0F;
            } else {
                fromY = reverse ? .0F : -from.getHeight() / 2;
                toY = reverse ? to.getHeight() : .0F;
            }

            timeline
                    .push(Tween.to(from, Translation.Y, duration).target(fromY))
                    .push(Tween.to(to, Translation.Y, duration).target(toY));
        }

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
