package ru.noties.screen.transit.tweens;

import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.screen.Screen;
import ru.noties.screen.transit.Edge;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.android.types.Rotation;
import ru.noties.tumbleweed.android.types.Translation;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CubeOutTweenEngine<K extends Enum<K>> extends TweenSwitchEngine<K> {

    protected static final boolean IS_M = Build.VERSION.SDK_INT == Build.VERSION_CODES.M;

    @NonNull
    public static <K extends Enum<K>> CubeOutTweenEngine<K> create(@NonNull Edge fromEdge, long duration) {

        final ViewTweenProvider provider;

        final float d = duration / 1000.F;

        switch (fromEdge) {

            case TOP:
                provider = new Top(d);
                break;

            case RIGHT:
                provider = new Right(d);
                break;

            case BOTTOM:
                provider = new Bottom(d);
                break;

            default:
                provider = new Left(d);
        }

        return new CubeOutTweenEngine<>(provider);
    }

    private final ViewTweenProvider provider;

    protected CubeOutTweenEngine(@NonNull ViewTweenProvider provider) {
        this.provider = provider;
    }

    @NonNull
    @Override
    protected BaseTweenDef createTween(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        return provider.provide(reverse, from.view(), to.view());
    }

    @Override
    protected void before(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        if (IS_M) {
            final View view = reverse ? from.manager().container() : to.manager().container();
            final int currentType = view.getLayerType();
            if (View.LAYER_TYPE_SOFTWARE != currentType) {
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                view.setTag(R.id.m_layer_type, currentType);
            }
        }
    }

    @Override
    protected void after(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        if (IS_M) {
            final View view = reverse ? from.manager().container() : to.manager().container();
            final Integer previousType = (Integer) view.getTag(R.id.m_layer_type);
            if (previousType != null) {
                view.setLayerType(previousType, null);
            }
        }
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

    private abstract static class Base implements ViewTweenProvider {

        final float duration;

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

            from.setRotationY(reverse ? 90 : 0);
            to.setRotationY(reverse ? 0 : -90);

            from.setTranslationX(reverse ? from.getWidth() : 0);
            to.setTranslationX(reverse ? 0 : -to.getWidth());

            from.setPivotX(.0F);
            to.setPivotX(to.getWidth());

            final float fromY = reverse ? 0 : 90;
            final float toY = reverse ? -90 : 0;

            final float fromX = reverse ? 0 : from.getWidth();
            final float toX = reverse ? -to.getWidth() : 0;

            return Timeline.createParallel()
                    .push(Tween.to(from, Rotation.Y, duration).target(fromY))
                    .push(Tween.to(to, Rotation.Y, duration).target(toY))
                    .push(Tween.to(from, Translation.X, duration).target(fromX))
                    .push(Tween.to(to, Translation.X, duration).target(toX));
        }
    }

    private static class Right extends Base {

        protected Right(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setRotationY(reverse ? -90 : 0);
            to.setRotationY(reverse ? 0 : 90);

            from.setTranslationX(reverse ? -from.getWidth() : 0);
            to.setTranslationX(reverse ? 0 : to.getWidth());

            from.setPivotX(from.getWidth());
            to.setPivotX(0);

            final float fromY = reverse ? 0 : -90;
            final float toY = reverse ? 90 : 0;

            final float fromX = reverse ? 0 : -from.getWidth();
            final float toX = reverse ? to.getWidth() : 0;

            return Timeline.createParallel()
                    .push(Tween.to(from, Rotation.Y, duration).target(fromY))
                    .push(Tween.to(to, Rotation.Y, duration).target(toY))
                    .push(Tween.to(from, Translation.X, duration).target(fromX))
                    .push(Tween.to(to, Translation.X, duration).target(toX));
        }
    }

    private static class Top extends Base {

        protected Top(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setRotationX(reverse ? -90 : 0);
            to.setRotationX(reverse ? 0 : 90);

            from.setTranslationY(reverse ? from.getHeight() : 0);
            to.setTranslationY(reverse ? 0 : -to.getHeight());

            from.setPivotY(0);
            to.setPivotY(to.getHeight());

            final float fromX = reverse ? 0 : -90;
            final float toX = reverse ? 90 : 0;

            final float fromY = reverse ? 0 : from.getHeight();
            final float toY = reverse ? -to.getHeight() : 0;

            return Timeline.createParallel()
                    .push(Tween.to(from, Rotation.X, duration).target(fromX))
                    .push(Tween.to(to, Rotation.X, duration).target(toX))
                    .push(Tween.to(from, Translation.Y, duration).target(fromY))
                    .push(Tween.to(to, Translation.Y, duration).target(toY));
        }
    }

    private static class Bottom extends Base {

        protected Bottom(float duration) {
            super(duration);
        }

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to) {

            from.setRotationX(reverse ? 90 : 0);
            to.setRotationX(reverse ? 0 : -90);

            from.setTranslationY(reverse ? -from.getHeight() : 0);
            to.setTranslationY(reverse ? 0 : to.getHeight());

            from.setPivotY(from.getHeight());
            to.setPivotY(0);

            final float fromX = reverse ? 0 : 90;
            final float toX = reverse ? -90 : 0;

            final float fromY = reverse ? 0 : -from.getHeight();
            final float toY = reverse ? to.getHeight() : 0;

            return Timeline.createParallel()
                    .push(Tween.to(from, Rotation.X, duration).target(fromX))
                    .push(Tween.to(to, Rotation.X, duration).target(toX))
                    .push(Tween.to(from, Translation.Y, duration).target(fromY))
                    .push(Tween.to(to, Translation.Y, duration).target(toY));
        }
    }
}
