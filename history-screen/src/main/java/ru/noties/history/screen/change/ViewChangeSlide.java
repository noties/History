package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings({"unused", "WeakerAccess"})
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

    @SuppressWarnings("SameParameterValue")
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

            final float fromX = reverse ? from.getWidth() : 0;
            final float toX = reverse ? 0 : -to.getWidth();

            from.setTranslationX(fromX);
            to.setTranslationX(toX);
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            final float fromX = reverse ? 0 : from.getWidth();
            final float toX = reverse ? -to.getWidth() : 0;

            from.animate()
                    .translationX(fromX)
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationX(toX)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class TopResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {

            final float fromY = reverse ? from.getHeight() : 0;
            final float toY = reverse ? 0 : -to.getHeight();

            from.setTranslationY(fromY);
            to.setTranslationY(toY);
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            final float fromY = reverse ? 0 : from.getHeight();
            final float toY = reverse ? -to.getHeight() : 0;

            from.animate()
                    .translationY(fromY)
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationY(toY)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class RightResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {

            final float fromX = reverse ? -from.getWidth() : 0;
            final float toX = reverse ? 0 : to.getWidth();

            from.setTranslationX(fromX);
            to.setTranslationX(toX);
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            final float fromX = reverse ? 0 : -from.getWidth();
            final float toX = reverse ? to.getWidth() : 0;

            from.animate()
                    .translationX(fromX)
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationX(toX)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    private class BottomResolver implements Resolver {

        @Override
        public void applyStart(boolean reverse, @NonNull View from, @NonNull View to) {

            final float fromY = reverse ? -from.getHeight() : 0;
            final float toY = reverse ? 0 : to.getHeight();

            from.setTranslationY(fromY);
            to.setTranslationY(toY);
        }

        @Override
        public void animate(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

            final float fromY = reverse ? 0 : -from.getHeight();
            final float toY = reverse ? to.getHeight() : 0;

            from.animate()
                    .translationY(fromY)
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationY(toY)
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }
}
