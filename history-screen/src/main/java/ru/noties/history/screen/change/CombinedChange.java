package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

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

    @NonNull
    @Override
    protected ChangeCallback applyNow(
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

        return new CombinedChangeCallback()
                .from(fromChange.apply(reverse, manager, reverse ? to : from, combinedAction))
                .to(toChange.apply(reverse, manager, reverse ? from : to, combinedAction));
    }
}
