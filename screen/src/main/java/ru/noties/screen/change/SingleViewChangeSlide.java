package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SingleViewChangeSlide extends SingleViewChange {

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> toLeft(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, LEFT | TO);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> fromLeft(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, LEFT | FROM);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> toTop(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, TOP | TO);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> fromTop(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, TOP | FROM);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> toRight(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, RIGHT | TO);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> fromRight(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, RIGHT | FROM);
    }

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> fromBottom(long duration) {
        //noinspection unchecked
        return new SingleViewChangeSlide(duration, BOTTOM);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    private static final int LEFT = 1 << 0;
    private static final int TOP = 1 << 1;
    private static final int RIGHT = 1 << 2;
    private static final int BOTTOM = 1 << 3;

    private static final int FROM = 1 << 4;
    private static final int TO = 1 << 5;

    private final long duration;
    private final Resolver resolver;

    SingleViewChangeSlide(long duration, int dir) {
        this.duration = duration;
        this.resolver = resolver(dir);
    }

    @NonNull
    private Resolver resolver(int dir) {

        final boolean from = (dir & FROM) == FROM;

        final Resolver resolver;

        if ((dir & TOP) == TOP) {
            resolver = new Top(from);
        } else if ((dir & RIGHT) == RIGHT) {
            resolver = new Right(from);
        } else if ((dir & BOTTOM) == BOTTOM) {
            resolver = new Bottom(from);
        } else {
            resolver = new Left(from);
        }

        return resolver;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        resolver.applyStart(reverse, view);
    }

    @Override
    protected void startAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view, @NonNull Runnable endAction) {
        resolver.animate(reverse, view, endAction);
    }

    @Override
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        view.clearAnimation();
    }

    private interface Resolver {

        void applyStart(boolean reverse, @NonNull View view);

        void animate(boolean reverse, @NonNull View view, @NonNull Runnable endAction);
    }

    private class Left implements Resolver {

        private final boolean from;

        Left(boolean from) {
            this.from = from;
        }

        @Override
        public void applyStart(boolean reverse, @NonNull View view) {
            if (!from) {
                reverse = !reverse;
            }
            view.setTranslationX(reverse ? .0F : -view.getWidth());
        }

        @Override
        public void animate(boolean reverse, @NonNull View view, @NonNull Runnable endAction) {

            if (!from) {
                reverse = !reverse;
            }

            view.animate()
                    .translationX(reverse ? -view.getWidth() : 0)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class Top implements Resolver {

        private final boolean from;

        Top(boolean from) {
            this.from = from;
        }

        @Override
        public void applyStart(boolean reverse, @NonNull View view) {
            if (!from) {
                reverse = !reverse;
            }
            view.setTranslationY(reverse ? .0F : -view.getHeight());
        }

        @Override
        public void animate(boolean reverse, @NonNull View view, @NonNull Runnable endAction) {

            if (!from) {
                reverse = !reverse;
            }

            view.animate()
                    .translationY(reverse ? -view.getHeight() : .0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class Right implements Resolver {

        private final boolean from;

        Right(boolean from) {
            this.from = from;
        }

        @Override
        public void applyStart(boolean reverse, @NonNull View view) {

            if (!from) {
                reverse = !reverse;
            }

            view.setTranslationX(reverse ? .0F : view.getWidth());
        }

        @Override
        public void animate(boolean reverse, @NonNull View view, @NonNull Runnable endAction) {

            if (!from) {
                reverse = !reverse;
            }

            view.animate()
                    .translationX(reverse ? view.getWidth() : .0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class Bottom implements Resolver {

        private final boolean from;

        Bottom(boolean from) {
            this.from = from;
        }

        @Override
        public void applyStart(boolean reverse, @NonNull View view) {

            if (!from) {
                reverse = !reverse;
            }

            view.setTranslationY(reverse ? view.getHeight() : 0);
        }

        @Override
        public void animate(boolean reverse, @NonNull View view, @NonNull Runnable endAction) {

            if (!from) {
                reverse = !reverse;
            }

            view.animate()
                    .translationY(reverse ? 0 : view.getHeight())
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }
}
