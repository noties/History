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
import ru.noties.tumbleweed.android.types.Rotation;

public class FlipViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new FlipViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new FlipViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new FlipViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new FlipViewChange(duration, false, false);
    }

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private FlipViewChange(float duration, boolean isHorizontal, boolean isStart) {
        this.duration = (duration / 1000.F) / 2;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? 90 : 0;
                toY = reverse ? 0 : -90;
            } else {
                fromY = reverse ? -90 : 0;
                toY = reverse ? 0 : 90;
            }

            from.setRotationY(fromY);
            to.setRotationY(toY);
        } else {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? -90 : 0;
                toX = reverse ? 0 : 90;
            } else {
                fromX = reverse ? 90 : 0;
                toX = reverse ? 0 : -90;
            }

            from.setRotationX(fromX);
            to.setRotationX(toX);
        }

        if (reverse) {
            from.setAlpha(.0F);
            to.setAlpha(1.F);
        } else {
            from.setAlpha(1.F);
            to.setAlpha(.0F);
        }
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createSequence();

        if (isHorizontal) {

            final float fromY;
            final float toY;

            if (isStart) {
                fromY = reverse ? 0 : 90;
                toY = reverse ? -90 : 0;
            } else {
                fromY = reverse ? 0 : -90;
                toY = reverse ? 90 : 0;
            }

            if (reverse) {
                timeline
                        .push(Tween.to(to, Rotation.Y, duration).target(toY))
                        .push(Tween.set(from, Alpha.VIEW).target(1.F))
                        .push(Tween.set(to, Alpha.VIEW).target(0))
                        .push(Tween.to(from, Rotation.Y, duration).target(fromY));
            } else {
                timeline
                        .push(Tween.to(from, Rotation.Y, duration).target(fromY))
                        .push(Tween.set(from, Alpha.VIEW).target(0))
                        .push(Tween.set(to, Alpha.VIEW).target(1.F))
                        .push(Tween.to(to, Rotation.Y, duration).target(toY));
            }
        } else {

            final float fromX;
            final float toX;

            if (isStart) {
                fromX = reverse ? 0 : -90;
                toX = reverse ? 90 : 0;
            } else {
                fromX = reverse ? 0 : 90;
                toX = reverse ? -90 : 0;
            }

            if (reverse) {
                timeline
                        .push(Tween.to(to, Rotation.X, duration).target(toX))
                        .push(Tween.set(from, Alpha.VIEW).target(1.F))
                        .push(Tween.set(to, Alpha.VIEW).target(0))
                        .push(Tween.to(from, Rotation.X, duration).target(fromX));
            } else {
                timeline
                        .push(Tween.to(from, Rotation.X, duration).target(fromX))
                        .push(Tween.set(from, Alpha.VIEW).target(0))
                        .push(Tween.set(to, Alpha.VIEW).target(1.F))
                        .push(Tween.to(to, Rotation.X, duration).target(toX));
            }
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
