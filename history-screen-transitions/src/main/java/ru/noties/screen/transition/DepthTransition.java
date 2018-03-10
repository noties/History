package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.tumbleweed.BaseTween;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.TweenCallback;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.types.Alpha;
import ru.noties.tumbleweed.android.types.Scale;
import ru.noties.tumbleweed.android.types.Translation;

public class DepthTransition extends TweenViewTransition {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration) {

        final DepthTransition transition;

        final float seconds = toSeconds(duration);

        switch (edge) {

            case LEFT:
                transition = new DepthTransition(seconds, new Left());
                break;

            case TOP:
                transition = new DepthTransition(seconds, new Top());
                break;

            case BOTTOM:
                transition = new DepthTransition(seconds, new Bottom());
                break;

            default:
                transition = new DepthTransition(seconds, new Right());
        }

        //noinspection unchecked
        return transition;
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration, @NonNull Class<K> type) {
        return create(edge, duration);
    }

    private static final float MIN_SCALE = .75F;

    private final float duration;
    private final Provider provider;

    private DepthTransition(float duration, @NonNull Provider provider) {
        this.duration = duration;
        this.provider = provider;
    }

    @Nullable
    @Override
    protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final TweenManager manager = tweenManagerParent(from);
        kill(manager, from, to);

        from.setAlpha(reverse ? .0F : 1.F);

        final float startScale = reverse ? MIN_SCALE : 1.F;
        from.setScaleX(startScale);
        from.setScaleY(startScale);

        final float endScale = reverse ? 1.F : MIN_SCALE;

        Timeline.createParallel()
                .push(Tween.to(from, Alpha.VIEW, duration).target(reverse ? 1.F : .0F))
                .push(Tween.to(from, Scale.XY, duration).target(endScale, endScale))
                .push(provider.provide(reverse, to, duration))
                .addCallback(TweenCallback.END, new TweenCallback() {
                    @Override
                    public void onEvent(int type, @NonNull BaseTween source) {
                        endAction.run();
                    }
                })
                .start(manager);

        return new TransitionCallback() {
            @Override
            public void cancel() {
                kill(manager, from, to);
                endAction.run();
            }
        };
    }

    private interface Provider {
        @NonNull
        BaseTweenDef provide(boolean reverse, @NonNull View to, float duration);
    }

    private static class Left implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View to, float duration) {
            to.setTranslationX(reverse ? .0F : -to.getWidth());
            return Tween.to(to, Translation.X, duration).target(reverse ? -to.getWidth() : .0F);
        }
    }

    private static class Right implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View to, float duration) {
            to.setTranslationX(reverse ? .0F : to.getWidth());
            return Tween.to(to, Translation.X, duration).target(reverse ? to.getWidth() : .0F);
        }
    }

    private static class Top implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View to, float duration) {
            to.setTranslationY(reverse ? .0F : -to.getHeight());
            return Tween.to(to, Translation.Y, duration).target(reverse ? -to.getHeight() : .0F);
        }
    }

    private static class Bottom implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View to, float duration) {
            to.setTranslationY(reverse ? .0F : to.getHeight());
            return Tween.to(to, Translation.Y, duration).target(reverse ? to.getHeight() : .0F);
        }
    }
}
