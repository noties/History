package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public abstract class ViewSwitch extends ScreenSwitch {

    @NonNull
    public final <K extends Enum<K>> ScreenSwitch<K> cast() {
        //noinspection unchecked
        return (ScreenSwitch<K>) this;
    }

    @NonNull
    public final <K extends Enum<K>> ScreenSwitch<K> cast(@NonNull Class<K> type) {
        //noinspection unchecked
        return (ScreenSwitch<K>) this;
    }

    @Override
    public final void apply(float fraction, @NonNull Screen from, @NonNull Screen to) {
        apply(fraction, from.view(), to.view());
    }

    protected abstract void apply(float fraction, @NonNull View from, @NonNull View to);
}
