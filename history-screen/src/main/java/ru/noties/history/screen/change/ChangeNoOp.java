package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public class ChangeNoOp<K extends Enum<K>> implements Change<K> {

    @NonNull
    public static <K extends Enum<K>> ChangeNoOp<K> instance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @NonNull
    @Override
    public ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return ChangeCallbackNoOp.noOp(endAction);
    }

    private static final ChangeNoOp INSTANCE = new ChangeNoOp();
}
