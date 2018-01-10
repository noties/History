package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

public abstract class ChangeController<K extends Enum<K>> {

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ChangeControllerBuilder<K> builder(@NonNull Class<K> type) {
        return new ChangeControllerBuilder<>();
    }

    @NonNull
    public static <K extends Enum<K>> ChangeController<K> create(@NonNull Change<K> change) {
        return new Impl<>(change);
    }

    @NonNull
    public static <K extends Enum<K>> ChangeController<K> create(
            @NonNull SingleChange<K> from,
            @NonNull SingleChange<K> to
    ) {
        return create(CombinedChange.create(from, to));
    }

    @NonNull
    public abstract ChangeCallback forward(
            @NonNull ScreenManager<K> manager,
            @Nullable Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @NonNull
    public abstract ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    /**
     * This method will be called when back operation removes multiple VISIBLE screens from layout.
     * Default implementation executes changes sequentially
     *
     * @param manager   {@link ScreenManager}
     * @param from      a list of {@link Screen} to be _popped_. Size will be more than 1 always,
     *                  otherwise simple {@link #back(ScreenManager, Screen, Screen, Runnable)} will be called
     * @param to        {@link Screen} to appear or null if all items are popped
     * @param endAction end action to be invoked after change completes
     * @return {@link ChangeCallback}
     */
    @SuppressWarnings("unused")
    @NonNull
    public ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        return to == null
                ? ChangeCallbackNoOp.noOp(endAction)
                : ChangeControllerBackMultiple.back(manager, this, from, to, endAction);
    }


    private static class Impl<K extends Enum<K>> extends ChangeController<K> {

        private final Change<K> change;

        Impl(@NonNull Change<K> change) {
            this.change = change;
        }

        @NonNull
        @Override
        public ChangeCallback forward(
                @NonNull ScreenManager<K> manager,
                @Nullable Screen<K, ? extends Parcelable> from,
                @NonNull Screen<K, ? extends Parcelable> to,
                @NonNull Runnable endAction
        ) {
            return from == null
                    ? ChangeCallbackNoOp.noOp(endAction)
                    : change.apply(false, manager, from, to, endAction);
        }

        @NonNull
        @Override
        public ChangeCallback back(
                @NonNull ScreenManager<K> manager,
                @NonNull Screen<K, ? extends Parcelable> from,
                @Nullable Screen<K, ? extends Parcelable> to,
                @NonNull Runnable endAction
        ) {
            return to == null
                    ? ChangeCallbackNoOp.noOp(endAction)
                    : change.apply(true, manager, to, from, endAction);
        }
    }
}
