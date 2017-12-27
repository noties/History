package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class HistoryChangedObserver<K extends Enum<K>> implements History.Observer<K> {

    public interface Action {
        void onHistoryChanged();
    }

    @NonNull
    public static <K extends Enum<K>> HistoryChangedObserver<K> create(@NonNull Action action) {
        return new Impl<>(action);
    }

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
