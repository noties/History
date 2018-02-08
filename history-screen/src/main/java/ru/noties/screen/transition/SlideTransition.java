package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class SlideTransition extends ViewTransition {

    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration) {

        final SlideTransition transition;

        switch (edge) {

            case LEFT:
                transition = new Horizontal(duration, new Left());
                break;

            case TOP:
                transition = new Vertical(duration, new Top());
                break;

//            case RIGHT:
//                break;

            case BOTTOM:
                transition = new Vertical(duration, new Bottom());
                break;

            default:
                transition = new Horizontal(duration, new Right());
        }

        //noinspection unchecked
        return transition;
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ScreenTransition<K> create(@NonNull Edge edge, long duration, @NonNull Class<K> type) {
        return create(edge, duration);
    }

    final long duration;
    final Provider provider;

    SlideTransition(long duration, @NonNull Provider provider) {
        this.duration = duration;
        this.provider = provider;
    }

    private interface Provider {

        float fromValue(@NonNull View from);

        float toValue(@NonNull View to);
    }

    private static class Horizontal extends SlideTransition {

        Horizontal(long duration, @NonNull Provider provider) {
            super(duration, provider);
        }

        @Nullable
        @Override
        protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

            from.clearAnimation();
            to.clearAnimation();

            final float fromValue = provider.fromValue(from);
            final float toValue = provider.toValue(to);

            from.setTranslationX(reverse ? fromValue : .0F);
            to.setTranslationX(reverse ? .0F : toValue);

            from.animate()
                    .setDuration(duration)
                    .translationX(reverse ? .0F : fromValue)
                    .start();

            to.animate()
                    .setDuration(duration)
                    .translationX(reverse ? toValue : .0F)
                    .withEndAction(endAction)
                    .start();

            return new TransitionCallback() {
                @Override
                public void cancel() {

                    from.clearAnimation();
                    to.clearAnimation();

                    endAction.run();
                }
            };
        }
    }

    private static class Vertical extends SlideTransition {

        Vertical(long duration, @NonNull Provider provider) {
            super(duration, provider);
        }

        @Nullable
        @Override
        protected TransitionCallback applyNow(boolean reverse, @NonNull final View from, @NonNull final View to, @NonNull final Runnable endAction) {

            from.clearAnimation();
            to.clearAnimation();

            final float fromValue = provider.fromValue(from);
            final float toValue = provider.toValue(to);

            from.setTranslationY(reverse ? fromValue : .0F);
            to.setTranslationY(reverse ? .0F : toValue);

            from.animate()
                    .setDuration(duration)
                    .translationY(reverse ? .0F : fromValue)
                    .start();

            to.animate()
                    .setDuration(duration)
                    .translationY(reverse ? toValue : .0F)
                    .withEndAction(endAction)
                    .start();

            return new TransitionCallback() {
                @Override
                public void cancel() {

                    from.clearAnimation();
                    to.clearAnimation();

                    endAction.run();
                }
            };
        }
    }

    private static class Left implements Provider {

        @Override
        public float fromValue(@NonNull View from) {
            return from.getWidth();
        }

        @Override
        public float toValue(@NonNull View to) {
            return -to.getWidth();
        }
    }

    private static class Right implements Provider {

        @Override
        public float fromValue(@NonNull View from) {
            return -from.getWidth();
        }

        @Override
        public float toValue(@NonNull View to) {
            return to.getWidth();
        }
    }

    private static class Top implements Provider {

        @Override
        public float fromValue(@NonNull View from) {
            return from.getHeight();
        }

        @Override
        public float toValue(@NonNull View to) {
            return -to.getHeight();
        }
    }

    private static class Bottom implements Provider {

        @Override
        public float fromValue(@NonNull View from) {
            return -from.getHeight();
        }

        @Override
        public float toValue(@NonNull View to) {
            return to.getHeight();
        }
    }
}
