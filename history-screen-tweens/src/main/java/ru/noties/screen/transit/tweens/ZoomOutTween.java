package ru.noties.screen.transit.tweens;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.screen.transit.Edge;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.TimelineDef;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.android.types.Alpha;
import ru.noties.tumbleweed.android.types.Scale;
import ru.noties.tumbleweed.android.types.Translation;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ZoomOutTween implements ViewTweenProvider {

    private static final float MIN_SCALE = .85F;
    private static final float MIN_ALPHA = .5F;

    @NonNull
    public static ZoomOutTween create(@NonNull Edge fromEdge, long duration) {

        final ZoomOutTween tween;

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

    protected ZoomOutTween(long duration) {
        this.duration = duration / 1000.F / 3;
    }

    @NonNull
    @Override
    public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

        applyStartValues(reverse, from, to);

        return Timeline.createSequence()
                .push(start(reverse, from, to))
                .push(position(reverse, from, to))
                .push(end(reverse, from, to));
    }

    protected void applyStartValues(boolean reverse, @NonNull View from, @NonNull View to) {

        final float fromAlpha = reverse ? MIN_ALPHA : 1.F;
        final float fromScale = reverse ? MIN_SCALE : 1.F;

        final float toScale = reverse ? 1.F : MIN_SCALE;
        final float toAlpha = reverse ? 1.F : MIN_ALPHA;

        from.setScaleX(fromScale);
        from.setScaleY(fromScale);

        from.setAlpha(fromAlpha);

        to.setScaleX(toScale);
        to.setScaleY(toScale);

        to.setAlpha(toAlpha);
    }

    @NonNull
    protected TimelineDef start(boolean reverse, @NonNull View from, @NonNull View to) {

        final View target = reverse ? to : from;

        return Timeline.createParallel()
                .push(Tween.to(target, Alpha.VIEW, duration).target(MIN_ALPHA))
                .push(Tween.to(target, Scale.XY, duration).target(MIN_SCALE, MIN_SCALE));
    }

    @NonNull
    protected TimelineDef end(boolean reverse, @NonNull View from, @NonNull View to) {

        final View target = reverse ? from : to;

        return Timeline.createParallel()
                .push(Tween.to(target, Alpha.VIEW, duration).target(1.F))
                .push(Tween.to(target, Scale.XY, duration).target(1.F, 1.F));
    }

    protected abstract TimelineDef position(boolean reverse, @NonNull View from, @NonNull View to);


    private static class Left extends ZoomOutTween {

        protected Left(long duration) {
            super(duration);
        }

        @Override
        protected TimelineDef position(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setTranslationX(reverse ? from.getWidth() : .0F);
            to.setTranslationX(reverse ? .0F : -to.getWidth());

            final float endFromX = reverse ? .0F : from.getWidth();
            final float endToX = reverse ? -to.getWidth() : .0F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.X, duration).target(endFromX))
                    .push(Tween.to(to, Translation.X, duration).target(endToX));
        }
    }

    private static class Right extends ZoomOutTween {

        protected Right(long duration) {
            super(duration);
        }

        @Override
        protected TimelineDef position(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setTranslationX(reverse ? -from.getWidth() : .0F);
            to.setTranslationX(reverse ? .0F : to.getWidth());

            final float endFromX = reverse ? .0F : -from.getWidth();
            final float endToX = reverse ? to.getWidth() : .0F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.X, duration).target(endFromX))
                    .push(Tween.to(to, Translation.X, duration).target(endToX));
        }
    }

    private static class Top extends ZoomOutTween {

        protected Top(long duration) {
            super(duration);
        }

        @Override
        protected TimelineDef position(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setTranslationY(reverse ? from.getHeight() : .0F);
            to.setTranslationY(reverse ? .0F : -to.getHeight());

            final float endFromY = reverse ? .0F : from.getHeight();
            final float endToY = reverse ? -to.getHeight() : .0F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.Y, duration).target(endFromY))
                    .push(Tween.to(to, Translation.Y, duration).target(endToY));
        }
    }

    private static class Bottom extends ZoomOutTween {

        protected Bottom(long duration) {
            super(duration);
        }

        @Override
        protected TimelineDef position(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setTranslationY(reverse ? -from.getHeight() : .0F);
            to.setTranslationY(reverse ? .0F : to.getHeight());

            final float endFromY = reverse ? .0F : -from.getHeight();
            final float endToY = reverse ? to.getHeight() : .0F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.Y, duration).target(endFromY))
                    .push(Tween.to(to, Translation.Y, duration).target(endToY));
        }
    }
}
