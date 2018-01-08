package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

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

    @SuppressWarnings("unused")
    @NonNull
    public ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull View view,
            @NonNull Runnable endAction
    ) {
        return back(manager, from.get(from.size() - 1), to, endAction);
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
