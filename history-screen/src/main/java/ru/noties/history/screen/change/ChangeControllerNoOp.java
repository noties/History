package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public final class ChangeControllerNoOp<K extends Enum<K>> extends ChangeController<K> {

    @NonNull
    public static <K extends Enum<K>> ChangeControllerNoOp<K> create() {
        return new ChangeControllerNoOp<>();
    }

    @NonNull
    @Override
    public ChangeCallback forward(@NonNull ScreenManager<K> manager, @Nullable Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
        return ChangeCallbackNoOp.noOp(endAction);
    }

    @NonNull
    @Override
    public ChangeCallback back(@NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @Nullable Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
        return ChangeCallbackNoOp.noOp(endAction);
    }

    private ChangeControllerNoOp() {
    }
}
