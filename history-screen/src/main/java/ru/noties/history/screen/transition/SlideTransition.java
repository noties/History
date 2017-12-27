package ru.noties.history.screen.transition;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SlideTransition extends AbsTransition {

    @NonNull
    public static SlideTransition left(long duration) {
        return new SlideTransition(LEFT, duration);
    }

    @NonNull
    public static SlideTransition top(long duration) {
        return new SlideTransition(TOP, duration);
    }

    @NonNull
    public static SlideTransition right(long duration) {
        return new SlideTransition(RIGHT, duration);
    }

    @NonNull
    public static SlideTransition bottom(long duration) {
        return new SlideTransition(BOTTOM, duration);
    }

    // start startEdge indicates TO view start
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {LEFT, TOP, RIGHT, BOTTOM})
    @interface StartEdge {
    }

    private final Resolver resolver;

    private final boolean isHorizontal;

    private final long duration;


    public SlideTransition(@StartEdge int startEdge, long duration) {
        this(startEdge, duration, null);
    }

    public SlideTransition(@StartEdge int startEdge, long duration, @Nullable Resolver resolver) {
        this.duration = duration;
        this.resolver = resolver != null
                ? resolver
                : createResolver(startEdge);
        this.isHorizontal = LEFT == startEdge || RIGHT == startEdge;
    }

    @Override
    protected void applyStartValues(@NonNull View from, @NonNull View to) {
        if (isHorizontal) {
            from.setTranslationX(resolver.fromStart(from));
            to.setTranslationX(resolver.toStart(to));
        } else {
            from.setTranslationY(resolver.fromStart(from));
            to.setTranslationY(resolver.toStart(to));
        }
    }

    @Override
    protected void runTransition(@NonNull View from, @NonNull View to, @NonNull Runnable endAction) {

        if (isHorizontal) {

            from.animate()
                    .translationX(resolver.fromEnd(from))
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationX(resolver.toEnd(to))
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        } else {

            from.animate()
                    .translationY(resolver.fromEnd(from))
                    .setDuration(duration)
                    .start();

            to.animate()
                    .translationY(resolver.toEnd(to))
                    .setDuration(duration)
                    .withEndAction(endAction)
                    .start();
        }
    }

    @Override
    protected void cancelTransition(@NonNull View from, @NonNull View to) {
        from.clearAnimation();
        to.clearAnimation();
    }

    @NonNull
    protected Resolver createResolver(@StartEdge int startEdge) {

        final Resolver resolver;

        switch (startEdge) {

            case LEFT:
                resolver = new ResolverLeft();
                break;

            case TOP:
                resolver = new ResolverTop();
                break;

            case RIGHT:
                resolver = new ResolverRight();
                break;

            case BOTTOM:
                resolver = new ResolverBottom();
                break;

            default:
                throw new RuntimeException();
        }

        return resolver;
    }

    public static abstract class Resolver {

        float fromStart(@NonNull View from) {
            return .0F;
        }

        abstract float toStart(@NonNull View to);

        abstract float fromEnd(@NonNull View from);

        float toEnd(@NonNull View to) {
            return .0F;
        }
    }

    protected static class ResolverLeft extends Resolver {

        @Override
        public float toStart(@NonNull View to) {
            return -to.getWidth();
        }

        @Override
        public float fromEnd(@NonNull View from) {
            return from.getWidth();
        }
    }

    protected static class ResolverRight extends Resolver {

        @Override
        float toStart(@NonNull View to) {
            return to.getWidth();
        }

        @Override
        float fromEnd(@NonNull View from) {
            return -from.getWidth();
        }
    }

    protected static class ResolverTop extends Resolver {

        @Override
        float toStart(@NonNull View to) {
            return -to.getHeight();
        }

        @Override
        float fromEnd(@NonNull View from) {
            return from.getHeight();
        }
    }

    protected static class ResolverBottom extends Resolver {

        @Override
        float toStart(@NonNull View to) {
            return to.getHeight();
        }

        @Override
        float fromEnd(@NonNull View from) {
            return -from.getHeight();
        }
    }
}
