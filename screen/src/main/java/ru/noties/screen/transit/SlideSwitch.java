package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("unused")
public abstract class SlideSwitch extends ViewSwitch {

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> fromLeft() {
        //noinspection unchecked
        return new Left();
    }

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> fromTop() {
        //noinspection unchecked
        return new Top();
    }

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> fromRight() {
        //noinspection unchecked
        return new Right();
    }

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> fromBottom() {
        //noinspection unchecked
        return new Bottom();
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
