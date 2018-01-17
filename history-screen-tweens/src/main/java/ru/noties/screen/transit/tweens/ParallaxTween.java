package ru.noties.screen.transit.tweens;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.screen.transit.Edge;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.android.types.Translation;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ParallaxTween implements ViewTweenProvider {

    @NonNull
    public static ParallaxTween create(@NonNull Edge fromEdge, long duration) {

        final ParallaxTween tween;

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

    ParallaxTween(long duration) {
        this.duration = duration / 1000.F;
    }


    private static class Left extends ParallaxTween {

        Left(long duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            final float startFromX = reverse ? from.getWidth() / 2 : .0F;
            final float startToX = reverse ? .0F : -to.getWidth();

            final float endFromX = reverse ? .0F : from.getWidth() / 2;
            final float endToX = reverse ? -to.getWidth() : .0F;

            return Timeline.createParallel()
                    .push(Tween.set(from, Translation.X).target(startFromX))
                    .push(Tween.set(to, Translation.X).target(startToX))
                    .push(Tween.to(from, Translation.X, duration).target(endFromX))
                    .push(Tween.to(to, Translation.X, duration).target(endToX));
        }
    }

    private static class Right extends ParallaxTween {

        Right(long duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            final float startFromX = reverse ? -from.getWidth() / 2 : .0F;
            final float startToX = reverse ? .0F : to.getWidth();

            final float endFromX = reverse ? .0F : -from.getWidth() / 2;
            final float endToX = reverse ? to.getWidth() : .0F;

            return Timeline.createParallel()
                    .push(Tween.set(from, Translation.X).target(startFromX))
                    .push(Tween.set(to, Translation.X).target(startToX))
                    .push(Tween.to(from, Translation.X, duration).target(endFromX))
                    .push(Tween.to(to, Translation.X, duration).target(endToX));
        }
    }

    private static class Top extends ParallaxTween {

        Top(long duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            final float startFromY = reverse ? from.getHeight() / 2 : .0F;
            final float startToY = reverse ? .0F : -to.getHeight();

            final float endFromY = reverse ? .0F : from.getHeight() / 2;
            final float endToY = reverse ? -to.getHeight() : .0F;

            return Timeline.createParallel()
                    .push(Tween.set(from, Translation.Y).target(startFromY))
                    .push(Tween.set(to, Translation.Y).target(startToY))
                    .push(Tween.to(from, Translation.Y, duration).target(endFromY))
                    .push(Tween.to(to, Translation.Y, duration).target(endToY));
        }
    }

    private static class Bottom extends ParallaxTween {

        Bottom(long duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            final float startFromY = reverse ? -from.getHeight() / 2 : .0F;
            final float startToY = reverse ? .0F : to.getHeight();

            final float endFromY = reverse ? .0F : -from.getHeight() / 2;
            final float endToY = reverse ? to.getHeight() : .0F;

            return Timeline.createParallel()
                    .push(Tween.set(from, Translation.Y).target(startFromY))
                    .push(Tween.set(to, Translation.Y).target(startToY))
                    .push(Tween.to(from, Translation.Y, duration).target(endFromY))
                    .push(Tween.to(to, Translation.Y, duration).target(endToY));
        }
    }
}
