package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public class ViewChangeSlide extends ViewChange {

    @NonNull
    public static <K extends Enum<K>> Change<K> fromLeft(long duration) {
        //noinspection unchecked
        return new ViewChangeSlide(duration, LEFT);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromTop(long duration) {
        //noinspection unchecked
        return new ViewChangeSlide(duration, TOP);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromRight(long duration) {
        //noinspection unchecked
        return new ViewChangeSlide(duration, RIGHT);
    }

    @NonNull
    public static <K extends Enum<K>> Change<K> fromBottom(long duration) {
        //noinspection unchecked
        return new ViewChangeSlide(duration, BOTTOM);
    }

    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;

    private final long duration;
    private final Resolver resolver;

    ViewChangeSlide(long duration, int dir) {
        this.duration = duration;
        this.resolver = resolver(dir);
    }

    @NonNull
    private Resolver resolver(int dir) {

        final Resolver resolver;

        switch (dir) {

            case TOP:
                resolver = new TopResolver();
                break;

            case RIGHT:
                resolver = new RightResolver();
                break;

            case BOTTOM:
                resolver = new BottomResolver();
                break;

            default:
                resolver = new LeftResolver();
        }

        return resolver;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        resolver.applyStart(
                reverse,
                from,
                to
        );
    }

    @Override
    protected void startAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    ) {
        resolver.animate(
                reverse,
                from,
                to,
                endAction
        );
    }

    @Override
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        from.clearAnimation();
        to.clearAnimation();
    }

    private interface Resolver {

        void applyStart(boolean reverse, @NonNull View from, @NonNull View to);

        void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction);
    }

    private class LeftResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {
            from.setTranslationX(.0F);
            to.setTranslationX(reverse ? to.getWidth() : -to.getWidth());
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            from.animate()
                    .translationX(reverse ? -from.getWidth() : from.getWidth())
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationX(.0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class TopResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {
            from.setTranslationY(.0F);
            to.setTranslationY(reverse ? to.getHeight() : -to.getHeight());
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            from.animate()
                    .translationY(reverse ? -from.getHeight() : from.getHeight())
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationY(.0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class RightResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {
            from.setTranslationX(.0F);
            to.setTranslationX(reverse ? -to.getWidth() : to.getWidth());
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            from.animate()
                    .translationX(reverse ? from.getWidth() : -from.getWidth())
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationX(.0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class BottomResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {
            from.setTranslationY(.0F);
            to.setTranslationY(reverse ? -to.getHeight() : to.getHeight());
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            from.animate()
                    .translationY(reverse ? from.getHeight() : -from.getHeight())
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationY(.0F)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }
}
