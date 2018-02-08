package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.tumbleweed.BaseTween;
import ru.noties.tumbleweed.Timeline;
import ru.noties.tumbleweed.Tween;
import ru.noties.tumbleweed.TweenCallback;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.types.Scale;

@SuppressWarnings("unused")
public abstract class AccordionTransition extends TweenViewTransition {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration) {

        final AccordionTransition transition;

        final float seconds = toSeconds(duration);

        switch (edge) {

            case LEFT:
                transition = new Horizontal(seconds, new Left());
                break;

            case TOP:
                transition = new Vertical(seconds, new Top());
                break;

//            case RIGHT:
//                break;

            case BOTTOM:
                transition = new Vertical(seconds, new Bottom());
                break;

            default:
                transition = new Horizontal(seconds, new Right());
                break;
        }

        //noinspection unchecked
        return transition;
    }

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration, @NonNull Class<K> type) {
        return create(edge, duration);
    }

    private interface Provider {

        float fromPivot(@NonNull View from);

        float toPivot(@NonNull View to);
    }

    final float duration;
    final Provider provider;

    AccordionTransition(float duration, @NonNull Provider provider) {
        this.duration = duration;
        this.provider = provider;
    }

    private static class Horizontal extends AccordionTransition {

        Horizontal(float duration, @NonNull Provider provider) {
            super(duration, provider);
        }

        @Nullable
        @Override
        protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

            final TweenManager manager = tweenManagerParent(from);
            kill(manager, from, to);

            from.setScaleX(reverse ? .0F : 1.F);
            to.setScaleX(reverse ? 1.F : .0F);

            from.setPivotX(provider.fromPivot(from));
            to.setPivotX(provider.toPivot(to));

            Timeline.createParallel()
                    .push(Tween.to(from, Scale.X, duration).target(reverse ? 1.F : .0F))
                    .push(Tween.to(to, Scale.X, duration).target(reverse ? .0F : 1.F))
                    .addCallback(TweenCallback.END, new TweenCallback() {
                        @Override
                        public void onEvent(int type, @NonNull BaseTween source) {
                            resetPivot(from, to);
                            endAction.run();
                        }
                    })
                    .start(manager);

            return new TransitionCallback() {
                @Override
                public void cancel() {
                    kill(manager, from, to);
                    resetPivot(from, to);
                    endAction.run();
                }
            };
        }
    }

    private static class Vertical extends AccordionTransition {

        Vertical(float duration, @NonNull Provider provider) {
            super(duration, provider);
        }

        @Nullable
        @Override
        protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

            final TweenManager manager = tweenManagerParent(from);
            kill(manager, from, to);

            from.setScaleY(reverse ? .0F : 1.F);
            to.setScaleY(reverse ? 1.F : .0F);

            from.setPivotY(provider.fromPivot(from));
            to.setPivotY(provider.toPivot(to));

            Timeline.createParallel()
                    .push(Tween.to(from, Scale.Y, duration).target(reverse ? 1.F : .0F))
                    .push(Tween.to(to, Scale.Y, duration).target(reverse ? .0F : 1.F))
                    .addCallback(TweenCallback.END, new TweenCallback() {
                        @Override
                        public void onEvent(int type, @NonNull BaseTween source) {
                            resetPivot(from, to);
                            endAction.run();
                        }
                    })
                    .start(manager);

            return new TransitionCallback() {
                @Override
                public void cancel() {
                    kill(manager, from, to);
                    resetPivot(from, to);
                    endAction.run();
                }
            };
        }
    }

    private static class Left implements Provider {

        @Override
        public float fromPivot(@NonNull View from) {
            return from.getWidth();
        }

        @Override
        public float toPivot(@NonNull View to) {
            return 0;
        }
    }

    private static class Right implements Provider {

        @Override
        public float fromPivot(@NonNull View from) {
            return 0;
        }

        @Override
        public float toPivot(@NonNull View to) {
            return to.getWidth();
        }
    }

    private static class Top implements Provider {

        @Override
        public float fromPivot(@NonNull View from) {
            return from.getHeight();
        }

        @Override
        public float toPivot(@NonNull View to) {
            return 0;
        }
    }

    private static class Bottom implements Provider {

        @Override
        public float fromPivot(@NonNull View from) {
            return 0;
        }

        @Override
        public float toPivot(@NonNull View to) {
            return to.getHeight();
        }
    }

    private static void resetPivot(@NonNull View from, @NonNull View to) {

        from.setPivotX(from.getWidth() / 2);
        from.setPivotY(from.getHeight() / 2);

        to.setPivotX(to.getWidth() / 2);
        to.setPivotY(to.getHeight() / 2);
    }
}
