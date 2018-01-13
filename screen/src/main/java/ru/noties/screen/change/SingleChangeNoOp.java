package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

@SuppressWarnings("unused")
public final class SingleChangeNoOp<K extends Enum<K>> implements SingleChange<K> {

    @NonNull
    public static <K extends Enum<K>> SingleChangeNoOp<K> instance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @Nullable
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

    private SingleChangeNoOp() {
    }
}
