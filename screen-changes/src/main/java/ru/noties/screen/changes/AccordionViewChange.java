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
import ru.noties.tumbleweed.android.types.Scale;

@SuppressWarnings("unused")
public class AccordionViewChange extends BaseViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new AccordionViewChange(duration, true, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new AccordionViewChange(duration, false, true);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new AccordionViewChange(duration, true, false);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new AccordionViewChange(duration, false, false);
    }

    private final float duration;
    private final boolean isHorizontal;
    private final boolean isStart;

    private AccordionViewChange(long duration, boolean isHorizontal, boolean isStart) {
        this.duration = duration / 1000.F;
        this.isHorizontal = isHorizontal;
        this.isStart = isStart;
    }


    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {

        if (isHorizontal) {

            from.setScaleX(reverse ? .0F : 1.F);
            to.setScaleX(reverse ? 1.F : .0F);

            from.setPivotX(isStart ? from.getWidth() : .0F);
            to.setPivotX(isStart ? .0F : to.getWidth());
        } else {

            from.setScaleY(reverse ? .0F : 1.F);
            to.setScaleY(reverse ? 1.F : .0F);

            from.setPivotY(isStart ? from.getHeight() : .0F);
            to.setPivotY(isStart ? .0F : to.getHeight());
        }
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ViewGroup container, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final TimelineDef timeline = Timeline.createParallel();

        if (isHorizontal) {
            timeline
                    .push(Tween.to(from, Scale.X, duration).target(reverse ? 1.F : .0F))
                    .push(Tween.to(to, Scale.X, duration).target(reverse ? .0F : 1.F));
        } else {
            timeline
                    .push(Tween.to(from, Scale.Y, duration).target(reverse ? 1.F : .0F))
                    .push(Tween.to(to, Scale.Y, duration).target(reverse ? .0F : 1.F));
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
        super.cancelChange(reverse, container, from, to);
    }
}
