package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("unused")
public abstract class SlideSwitch extends ViewSwitch {

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> from(@NonNull Edge edge) {

        final SlideSwitch slideSwitch;

        switch (edge) {

            case TOP:
                slideSwitch = new Top();
                break;

            case RIGHT:
                slideSwitch = new Right();
                break;

            case BOTTOM:
                slideSwitch = new Bottom();
                break;

            default:
                slideSwitch = new Left();
        }

        //noinspection unchecked
        return slideSwitch;
    }

    @Override
    protected abstract void apply(float fraction, @NonNull View from, @NonNull View to);


    private static class Left extends SlideSwitch {

        @Override
        protected void apply(float fraction, @NonNull View from, @NonNull View to) {
            from.setTranslationX(fraction * from.getWidth());
            to.setTranslationX(-(1.F - fraction) * to.getWidth());
        }
    }

    private static class Right extends SlideSwitch {

        @Override
        protected void apply(float fraction, @NonNull View from, @NonNull View to) {
            from.setTranslationX(-fraction * from.getWidth());
            to.setTranslationX((1.F - fraction) * to.getWidth());
        }
    }

    private static class Top extends SlideSwitch {

        @Override
        protected void apply(float fraction, @NonNull View from, @NonNull View to) {
            from.setTranslationY(fraction * from.getHeight());
            to.setTranslationY(-(1.F - fraction) * to.getHeight());
        }
    }

    private static class Bottom extends SlideSwitch {

        @Override
        protected void apply(float fraction, @NonNull View from, @NonNull View to) {
            from.setTranslationY(-fraction * from.getHeight());
            to.setTranslationY((1.F - fraction) * to.getHeight());
        }
    }
}
