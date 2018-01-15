package ru.noties.screen.transit;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public abstract class SwitchController<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> SwitchController<K> create(@NonNull SwitchEngine<K> switchEngine) {
        return new Impl<>(switchEngine);
    }

    @NonNull
    public static <K extends Enum<K>> SwitchControllerBuilder<K> builder() {
        return new SwitchControllerBuilder<>();
    }

    @NonNull
    public static <K extends Enum<K>> SwitchControllerBuilder<K> builder(@NonNull Class<K> type) {
        return new SwitchControllerBuilder<>();
    }

    @Nullable
    public SwitchEngineCallback forward(
            @Nullable Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return from == null
                ? SwitchEngineCallback.noOp(endAction)
                : apply(false, from, to, endAction);
    }

    @Nullable
    public SwitchEngineCallback back(
            @NonNull Screen<K, ? extends Parcelable> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return to == null
                ? SwitchEngineCallback.noOp(endAction)
                : apply(true, to, from, endAction);
    }

    @Nullable
    public SwitchEngineCallback back(
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return to == null
                ? SwitchEngineCallback.noOp(endAction)
                : SwitchControllerBackMultiple.back(this, from, to, endAction);
    }

    @Nullable
    protected abstract SwitchEngineCallback apply(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @NonNull
    public abstract SwitchEngine<K> switchEngine(
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );

    private static class Impl<K extends Enum<K>> extends SwitchController<K> {

        private final SwitchEngine<K> switchEngine;

        Impl(@NonNull SwitchEngine<K> switchEngine) {
            this.switchEngine = switchEngine;
        }

        @Nullable
        @Override
        protected SwitchEngineCallback apply(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return switchEngine.apply(reverse, from, to, endAction);
        }

        @NonNull
        @Override
        public SwitchEngine<K> switchEngine(@NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
            return switchEngine;
        }
    }
}
