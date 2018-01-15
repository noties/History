package ru.noties.screen.transit;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.screen.Screen;

public abstract class ScreenSwitch<K extends Enum<K>> {

    public abstract void apply(
            float fraction,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );
}
