package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("unused")
public class AlphaSwitch extends ViewSwitch {

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> create() {
        //noinspection unchecked
        return new AlphaSwitch();
    }

    @Override
    protected void apply(float fraction, @NonNull View from, @NonNull View to) {
        from.setAlpha(1.F - fraction);
        to.setAlpha(fraction);
    }
}
