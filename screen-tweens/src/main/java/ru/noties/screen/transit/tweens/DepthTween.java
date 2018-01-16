package ru.noties.screen.transit.tweens;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.screen.transit.Edge;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.TweenDef;
import ru.noties.tumbleweed.android.types.Alpha;
import ru.noties.tumbleweed.android.types.Scale;
import ru.noties.tumbleweed.android.types.Translation;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class DepthTween implements ViewTweenProvider {

    private static final float MIN_SCALE = .75F;

    @NonNull
    public static DepthTween create(@NonNull Edge fromEdge, long duration) {

        final DepthTween tween;

        switch (fromEdge) {

            case TOP:
                tween = new Top(duration);
                break;

            case RIGHT:
                tween = new Right(duration);
                break;

            case BOTTOM:
                tween = new Bottom(duration);
                break;

            default:
                tween = new Left(duration);
        }

        return tween;
    }

    protected final float duration;

    protected DepthTween(long duration) {
        this.duration = duration / 1000.F;
    }

    @NonNull
    @Override
    public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

        applyStartValues(reverse, from);

        final float fromAlpha = reverse ? 1.F : .0F;
        final float fromScale = reverse ? 1.F : MIN_SCALE;

        return Timeline.createParallel()
                .push(Tween.to(from, Alpha.VIEW, duration).target(fromAlpha))
                .push(Tween.to(from, Scale.XY, duration).target(fromScale, fromScale))
                .push(position(reverse, from, to));
    }

    protected void applyStartValues(boolean reverse, @NonNull View from) {

        from.setAlpha(reverse ? .0F : 1.F);

        final float scale = reverse ? MIN_SCALE : 1.F;
        from.setScaleX(scale);
        from.setScaleY(scale);
    }

    protected abstract TweenDef<?> position(boolean reverse, @NonNull View from, @NonNull View to);

    private static class Left extends DepthTween {

        protected Left(long duration) {
            super(duration);
        }

        @Override
        protected TweenDef<?> position(boolean reverse, @NonNull View from, @NonNull View to) {
            to.setTranslationX(reverse ? .0F : -to.getWidth());
            return Tween.to(to, Translation.X, duration).target(reverse ? -to.getWidth() : .0F);
        }
    }

    private static class Right extends DepthTween {

        protected Right(long duration) {
            super(duration);
        }

        @Override
        protected TweenDef<?> position(boolean reverse, @NonNull View from, @NonNull View to) {
            to.setTranslationX(reverse ? .0F : to.getWidth());
            return Tween.to(to, Translation.X, duration).target(reverse ? to.getWidth() : .0F);
        }
    }

    private static class Top extends DepthTween {

        protected Top(long duration) {
            super(duration);
        }

        @Override
        protected TweenDef<?> position(boolean reverse, @NonNull View from, @NonNull View to) {
            to.setTranslationY(reverse ? .0F : -to.getHeight());
            return Tween.to(to, Translation.Y, duration).target(reverse ? -to.getHeight() : .0F);
        }
    }

    private static class Bottom extends DepthTween {

        protected Bottom(long duration) {
            super(duration);
        }

        @Override
        protected TweenDef<?> position(boolean reverse, @NonNull View from, @NonNull View to) {
            to.setTranslationY(reverse ? .0F : to.getHeight());
            return Tween.to(to, Translation.Y, duration).target(reverse ? to.getHeight() : .0F);
        }
    }
}
