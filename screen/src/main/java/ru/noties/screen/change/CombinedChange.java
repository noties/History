package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

public class CombinedChange<K extends Enum<K>> extends Change<K> {

    @NonNull
    public static <K extends Enum<K>> CombinedChange<K> create(
            @NonNull SingleChange<K> from,
            @NonNull SingleChange<K> to
    ) {
        return new CombinedChange<>(from, to);
    }

    private final SingleChange<K> fromChange;
    private final SingleChange<K> toChange;

    @SuppressWarnings("WeakerAccess")
    public CombinedChange(@NonNull SingleChange<K> fromChange, @NonNull SingleChange<K> toChange) {
        this.fromChange = fromChange;
        this.toChange = toChange;
    }

    @Nullable
    @Override
    protected final ChangeCallback applyNow(
            boolean reverse,
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull final Runnable endAction
    ) {

        final Runnable combinedAction = new Runnable() {

            private int count = 0;

            @Override
            public void run() {
                if (++count == 2) {
                    endAction.run();
                }
            }
        };

        final ChangeCallback fromCallback = fromChange.apply(reverse, manager, from, combinedAction);
        final ChangeCallback toCallback = toChange.apply(reverse, manager, to, combinedAction);

        if (fromCallback != null || toCallback != null) {
            return new CombinedChangeCallback()
                    .from(fromCallback)
                    .to(toCallback);
        } else {
            return null;
        }
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        throw new RuntimeException();
    }

    @Override
    protected void executeChange(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
        throw new RuntimeException();
    }

    @Override
    protected void cancelChange(boolean reverse, @NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        throw new RuntimeException();
    }
}
