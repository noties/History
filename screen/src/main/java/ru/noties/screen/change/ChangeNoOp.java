package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

public final class ChangeNoOp<K extends Enum<K>> extends Change<K> {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <K extends Enum<K>> ChangeNoOp<K> instance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @Nullable
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
    protected void applyStartValues(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        // no op
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
        // no op
    }

    @Override
    protected void cancelChange(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        // no op
    }

    @Override
    protected boolean isReady(@NonNull Screen<K, ? extends Parcelable> screen) {
        return true;
    }

    private static final ChangeNoOp INSTANCE = new ChangeNoOp();

    private ChangeNoOp() {
    }
}
