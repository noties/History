package ru.noties.screen.transition;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.tumbleweed.BaseTween;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.TweenCallback;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.types.Rotation;
import ru.noties.tumbleweed.android.types.Translation;

@SuppressWarnings({"unused", "WeakerAccess"})
public class CubeOutTransition extends TweenViewTransition {

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration) {

        final CubeOutTransition transition;

        final float seconds = toSeconds(duration);

        switch (edge) {

            case LEFT:
                transition = new CubeOutTransition(seconds, new Left());
                break;

            case TOP:
                transition = new CubeOutTransition(seconds, new Top());
                break;

//            case RIGHT:
//                break;

            case BOTTOM:
                transition = new CubeOutTransition(seconds, new Bottom());
                break;

            default:
                transition = new CubeOutTransition(seconds, new Right());
                break;
        }

        //noinspection unchecked
        return transition;
    }

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration, @NonNull Class<K> type) {
        return create(edge, duration);
    }

    private static final boolean IS_M = Build.VERSION.SDK_INT == Build.VERSION_CODES.M;

    private final float duration;
    private final Provider provider;

    private CubeOutTransition(float duration, @NonNull Provider provider) {
        this.duration = duration;
        this.provider = provider;
    }

    @Nullable
    @Override
    protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final View container = parent(from);
        final TweenManager manager = tweenManager(container);
        kill(manager, from, to);

        before(container);

        provider.provide(reverse, from, to, duration)
                .addCallback(TweenCallback.END, new TweenCallback() {
                    @Override
                    public void onEvent(int type, @NonNull BaseTween source) {
                        resetPivot(from, to);
                        after(container);
                        endAction.run();
                    }
                })
                .start(manager);

        return new TransitionCallback() {
            @Override
            public void cancel() {
                kill(manager, from, to);
                resetPivot(from, to);
                after(container);
                endAction.run();
            }
        };
    }

    private void before(@NonNull View container) {
        if (IS_M) {
            final int currentType = container.getLayerType();
            if (View.LAYER_TYPE_SOFTWARE != currentType) {
                container.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                container.setTag(R.id.m_layer_type, currentType);
            }
        }
    }

    private void after(@NonNull View container) {
        if (IS_M) {
            final Integer previousType = (Integer) container.getTag(R.id.m_layer_type);
            if (previousType != null) {
                container.setLayerType(previousType, null);
            }
        }
    }

    private void resetPivot(@NonNull View from, @NonNull View to) {

        from.setPivotX(from.getWidth() / 2);
        from.setPivotY(from.getHeight() / 2);

        to.setPivotX(to.getWidth() / 2);
        to.setPivotY(to.getHeight() / 2);
    }

    private interface Provider {
        @NonNull
        BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration);
    }

    private static class Left implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

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

    private static class Right implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

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

    private static class Top implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

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

    private static class Bottom implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

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
