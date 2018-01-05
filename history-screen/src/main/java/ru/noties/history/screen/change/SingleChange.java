package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public interface SingleChange<K extends Enum<K>> {

    @NonNull
    ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> screen,
            @NonNull Runnable endAction
    );

}
