package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

public interface SingleChange<K extends Enum<K>> {

    @Nullable
    ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> screen,
            @NonNull Runnable endAction
    );
}
