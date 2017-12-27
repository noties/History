package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class HistoryObserverAdapter<K extends Enum<K>> implements History.Observer<K> {
    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {

    }

    @Override
    public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {

    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {

    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {

    }
}
