package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public class SingleChangeNoOp<K extends Enum<K>> implements SingleChange<K> {

    @NonNull
    public static <K extends Enum<K>> SingleChangeNoOp<K> instance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @NonNull
    @Override
    public ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> screen,
            @NonNull Runnable endAction
    ) {
        return ChangeCallbackNoOp.noOp(endAction);
    }

    private static final SingleChangeNoOp INSTANCE = new SingleChangeNoOp();
}
