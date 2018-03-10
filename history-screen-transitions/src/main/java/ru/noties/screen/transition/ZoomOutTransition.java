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

public class ZoomOutTransition extends TweenViewTransition {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration) {

        final ZoomOutTransition transition;

        final float seconds = toSeconds(duration);

        switch (edge) {

            case LEFT:
                transition = new ZoomOutTransition(seconds, new Left());
                break;

            case TOP:
                transition = new ZoomOutTransition(seconds, new Top());
                break;

            case BOTTOM:
                transition = new ZoomOutTransition(seconds, new Bottom());
                break;

            default:
                transition = new ZoomOutTransition(seconds, new Right());
        }

        //noinspection unchecked
        return transition;
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration, @NonNull Class<K> type) {
        return create(edge, duration);
    }

    private static final float MIN_SCALE = .85F;
    private static final float MIN_ALPHA = .5F;

    private final float duration;
    private final Provider provider;

    private ZoomOutTransition(float duration, @NonNull Provider provider) {
        this.duration = duration;
        this.provider = provider;
    }

    @Nullable
    @Override
    protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

        final TweenManager manager = tweenManagerParent(from);
        kill(manager, from, to);

        applyStartValues(reverse, from, to);

        Timeline.createSequence()
                .push(start(reverse, from, to))
                .push(provider.provide(reverse, from, to, duration))
                .push(end(reverse, from, to))
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

    private void applyStartValues(boolean reverse, @NonNull View from, @NonNull View to) {

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
    private BaseTweenDef start(boolean reverse, @NonNull View from, @NonNull View to) {

        final View target = reverse ? to : from;

        return Timeline.createParallel()
                .push(Tween.to(target, Alpha.VIEW, duration).target(MIN_ALPHA))
                .push(Tween.to(target, Scale.XY, duration).target(MIN_SCALE, MIN_SCALE));
    }

    @NonNull
    private BaseTweenDef end(boolean reverse, @NonNull View from, @NonNull View to) {

        final View target = reverse ? from : to;

        return Timeline.createParallel()
                .push(Tween.to(target, Alpha.VIEW, duration).target(1.F))
                .push(Tween.to(target, Scale.XY, duration).target(1.F, 1.F));
    }

    private interface Provider {

        @NonNull
        BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration);
    }

    private static class Left implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

            from.setTranslationX(reverse ? from.getWidth() : .0F);
            to.setTranslationX(reverse ? .0F : -to.getWidth());

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.X, duration).target(reverse ? .0F : from.getWidth()))
                    .push(Tween.to(to, Translation.X, duration).target(reverse ? -to.getWidth() : .0F));
        }
    }

    private static class Right implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

            from.setTranslationX(reverse ? -from.getWidth() : .0F);
            to.setTranslationX(reverse ? .0F : to.getWidth());

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.X, duration).target(reverse ? .0F : -from.getWidth()))
                    .push(Tween.to(to, Translation.X, duration).target(reverse ? to.getWidth() : .0F));
        }
    }

    private static class Top implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

            from.setTranslationY(reverse ? from.getHeight() : .0F);
            to.setTranslationY(reverse ? .0F : -to.getHeight());

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.Y, duration).target(reverse ? .0F : from.getHeight()))
                    .push(Tween.to(to, Translation.Y, duration).target(reverse ? -to.getHeight() : .0F));
        }
    }

    private static class Bottom implements Provider {

        @NonNull
        @Override
        public BaseTweenDef provide(boolean reverse, @NonNull View from, @NonNull View to, float duration) {

            from.setTranslationY(reverse ? -from.getHeight() : .0F);
            to.setTranslationY(reverse ? .0F : to.getHeight());

            return Timeline.createParallel()
                    .push(Tween.to(from, Translation.Y, duration).target(reverse ? .0F : -from.getHeight()))
                    .push(Tween.to(to, Translation.Y, duration).target(reverse ? to.getHeight() : .0F));
        }
    }
}
