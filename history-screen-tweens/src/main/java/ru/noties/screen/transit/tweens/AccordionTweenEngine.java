package ru.noties.screen.transit.tweens;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.screen.Screen;
import ru.noties.screen.transition.Edge;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.android.types.Scale;

@SuppressWarnings("WeakerAccess")
public class AccordionTweenEngine<K extends Enum<K>> extends TweenSwitchEngine<K> {

    @NonNull
    public static <K extends Enum<K>> AccordionTweenEngine<K> create(@NonNull Edge fromEdge, long duration) {

        final float d = duration / 1000.F;

        final Base base;

        switch (fromEdge) {

            case TOP:
                base = new Top(d);
                break;

            case RIGHT:
                base = new Right(d);
                break;

            case BOTTOM:
                base = new Bottom(d);
                break;

            default:
                base = new Left(d);
        }

        return new AccordionTweenEngine<>(base);
    }

    private final Base base;

    AccordionTweenEngine(@NonNull Base base) {
        this.base = base;
    }

    @NonNull
    @Override
    protected BaseTweenDef createTween(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        return base.provide(reverse, from.view(), to.view());
    }

    @Override
    protected void after(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        resetPivot(from.view(), to.view());
    }

    private void resetPivot(@Nullable View from, @Nullable View to) {

        // end action could detach a view, so no need to reset its pivot
        if (from != null) {
            from.setPivotX(from.getWidth() / 2);
            from.setPivotY(from.getHeight() / 2);
        }

        if (to != null) {
            to.setPivotX(to.getWidth() / 2);
            to.setPivotY(to.getHeight() / 2);
        }
    }

    private static abstract class Base implements ViewTweenProvider {

        protected final float duration;

        protected Base(float duration) {
            this.duration = duration;
        }
    }

    private static class Left extends Base {

        protected Left(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setScaleX(reverse ? .0F : 1.F);
            to.setScaleX(reverse ? 1.F : .0F);

            from.setPivotX(from.getWidth());
            to.setPivotX(.0F);

            final float fromScale = reverse ? 1.F : .0F;
            final float toScale = reverse ? .0F : 1.F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Scale.X, duration).target(fromScale))
                    .push(Tween.to(to, Scale.X, duration).target(toScale));
        }
    }

    private static class Right extends Base {

        protected Right(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setScaleX(reverse ? .0F : 1.F);
            to.setScaleX(reverse ? 1.F : .0F);

            from.setPivotX(.0F);
            to.setPivotX(to.getWidth());

            final float fromScale = reverse ? 1.F : .0F;
            final float toScale = reverse ? .0F : 1.F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Scale.X, duration).target(fromScale))
                    .push(Tween.to(to, Scale.X, duration).target(toScale));
        }
    }

    private static class Top extends Base {

        protected Top(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setScaleY(reverse ? .0F : 1.F);
            to.setScaleY(reverse ? 1.F : .0F);

            from.setPivotY(from.getHeight());
            to.setPivotY(.0F);

            final float fromScale = reverse ? 1.F : .0F;
            final float toScale = reverse ? .0F : 1.F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Scale.Y, duration).target(fromScale))
                    .push(Tween.to(to, Scale.Y, duration).target(toScale));
        }
    }

    private static class Bottom extends Base {

        protected Bottom(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setScaleY(reverse ? .0F : 1.F);
            to.setScaleY(reverse ? 1.F : .0F);

            from.setPivotY(.0F);
            to.setPivotY(to.getHeight());

            final float fromScale = reverse ? 1.F : .0F;
            final float toScale = reverse ? .0F : 1.F;

            return Timeline.createParallel()
                    .push(Tween.to(from, Scale.Y, duration).target(fromScale))
                    .push(Tween.to(to, Scale.Y, duration).target(toScale));
        }
    }
}
