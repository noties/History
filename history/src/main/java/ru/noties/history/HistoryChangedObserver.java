package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * An implementation of {@link ru.noties.history.History.Observer} to listen for history changes.
 * Can be extended or created via {@link #create(Action)} method
 *
 * @see #create(Action)
 * @see History#observe(History.Observer)
 */
@SuppressWarnings("unused")
public abstract class HistoryChangedObserver<K extends Enum<K>> implements History.Observer<K> {

    /**
     * Simple interface to be used when creating default implementation via {@link #create(Action)}
     */
    public interface Action {

        /**
         * @see #onHistoryChanged()
         */
        void onHistoryChanged();
    }

    /**
     * Factory method to obtain an instance of {@link HistoryChangedObserver}
     *
     * @param action to be triggered when history is changed
     * @return a new instance of {@link HistoryChangedObserver}
     */
    @NonNull
    public static <K extends Enum<K>> HistoryChangedObserver<K> create(@NonNull Action action) {
        return new Impl<>(action);
    }


    /**
     * This method will be called when {@link History} is changed
     */
    public abstract void onHistoryChanged();


    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        onHistoryChanged();
    }

    @Override
    public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        onHistoryChanged();
    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {
        onHistoryChanged();
    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {
        onHistoryChanged();
    }


    private static class Impl<K extends Enum<K>> extends HistoryChangedObserver<K> {

        private final Action action;

        Impl(@NonNull Action action) {
            this.action = action;
        }

        @Override
        public void onHistoryChanged() {
            action.onHistoryChanged();
        }
    }
}
