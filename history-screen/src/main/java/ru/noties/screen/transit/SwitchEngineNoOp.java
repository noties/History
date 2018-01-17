package ru.noties.screen.transit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public class SwitchEngineNoOp extends SwitchEngine {

    @NonNull
    public static <K extends Enum<K>> SwitchEngine<K> create() {
        //noinspection unchecked
        return new SwitchEngineNoOp();
    }

    @NonNull
    public static <K extends Enum<K>> SwitchEngine<K> create(@NonNull Class<K> type) {
        //noinspection unchecked
        return new SwitchEngineNoOp();
    }

    private SwitchEngineNoOp() {
        //noinspection unchecked,ConstantConditions
        super(null);
    }

    @Nullable
    @Override
    public SwitchEngineCallback apply(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        return SwitchEngineCallback.noOp(endAction);
    }

    @Nullable
    @Override
    protected SwitchEngineCallback applyNow(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        throw new RuntimeException();
    }
}
