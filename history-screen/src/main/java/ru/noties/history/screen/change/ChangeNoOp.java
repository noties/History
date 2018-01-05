package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public final class ChangeNoOp<K extends Enum<K>> extends Change<K> {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <K extends Enum<K>> ChangeNoOp<K> instance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @NonNull
    @Override
    protected ChangeCallback applyNow(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return ChangeCallbackNoOp.noOp(endAction);
    }

    @Override
    protected boolean isReady(@NonNull Screen<K, ? extends Parcelable> screen) {
        return true;
    }

    private static final ChangeNoOp INSTANCE = new ChangeNoOp();

    private ChangeNoOp() {
    }
}
