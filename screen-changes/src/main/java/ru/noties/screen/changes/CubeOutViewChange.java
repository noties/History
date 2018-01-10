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
import ru.noties.tumbleweed.android.types.Rotation;
import ru.noties.tumbleweed.android.types.Translation;

public class CubeOutViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new CubeOutViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new CubeOutViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new CubeOutViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new CubeOutViewChange(duration, false, false);
    }

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private CubeOutViewChange(float duration, boolean isHorizontal, boolean isStart) {
        this.duration = duration / 1000.F;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            final float fromY;
            final float toY;

            final float fromX;
            final float toX;

            if (isStart) {

                fromY = reverse ? 90 : 0;
                toY = reverse ? 0 : -90;

                fromX = reverse ? from.getWidth() : .0F;
                toX = reverse ? .0F : -to.getWidth();

            } else {
                fromY = reverse ? -90 : 0;
                toY = reverse ? 0 : 90;

                fromX = reverse ? -from.getWidth() : .0F;
                toX = reverse ? .0F : to.getWidth();
            }

            from.setRotationY(fromY);
            to.setRotationY(toY);

            from.setTranslationX(fromX);
            to.setTranslationX(toX);

            from.setPivotX(isStart ? .0F : from.getWidth());
            to.setPivotX(isStart ? to.getWidth() : .0F);
        } else {

            final float fromX;
            final float toX;

            final float fromY;
            final float toY;

            if (isStart) {

                fromX = reverse ? -90 : 0;
                toX = reverse ? 0 : 90;

                fromY = reverse ? from.getHeight() : .0F;
                toY = reverse ? .0F : -to.getHeight();

            } else {

                fromX = reverse ? 90 : 0;
                toX = reverse ? 0 : -90;

                fromY = reverse ? -from.getHeight() : .0F;
                toY = reverse ? .0F : to.getHeight();
            }

            from.setRotationX(fromX);
            to.setRotationX(toX);

            from.setTranslationY(fromY);
            to.setTranslationY(toY);

            from.setPivotY(isStart ? .0F : from.getHeight());
            to.setPivotY(isStart ? to.getHeight() : .0F);
        }
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createParallel();

        if (isHorizontal) {

            final float fromY;
            final float toY;

            final float fromX;
            final float toX;

            if (isStart) {

                fromY = reverse ? .0F : 90;
                toY = reverse ? -90 : .0F;

                fromX = reverse ? .0F : from.getWidth();
                toX = reverse ? -to.getWidth() : .0F;

            } else {

                fromY = reverse ? 0 : -90;
                toY = reverse ? 90 : 0;

                fromX = reverse ? .0F : -from.getWidth();
                toX = reverse ? to.getWidth() : .0F;
            }

            timeline
                    .push(Tween.to(from, Rotation.Y, duration).target(fromY))
                    .push(Tween.to(to, Rotation.Y, duration).target(toY))
                    .push(Tween.to(from, Translation.X, duration).target(fromX))
                    .push(Tween.to(to, Translation.X, duration).target(toX));
        } else {

            final float fromX;
            final float toX;

            final float fromY;
            final float toY;

            if (isStart) {

                fromX = reverse ? 0 : -90;
                toX = reverse ? 90 : 0;

                fromY = reverse ? .0F : from.getHeight();
                toY = reverse ? -to.getHeight() : .0F;

            } else {

                fromX = reverse ? 0 : 90;
                toX = reverse ? -90 : 0;

                fromY = reverse ? .0F : -from.getHeight();
                toY = reverse ? to.getHeight() : .0F;
            }

            timeline
                    .push(Tween.to(from, Rotation.X, duration).target(fromX))
                    .push(Tween.to(to, Rotation.X, duration).target(toX))
                    .push(Tween.to(from, Translation.Y, duration).target(fromY))
                    .push(Tween.to(to, Translation.Y, duration).target(toY));
        }

        timeline
                .callback(TweenCallback.END, new TweenCallback() {
                    @Override
                    public void onEvent(int type, @NonNull BaseTween source) {
                        resetPivot(from, to);
                        endAction.run();
                    }
                })
                .start(tweenManager(container));
    }

    @Override
    protected void cancelChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        resetPivot(from, to);
        tweenManager(container).killAll();
    }
}
