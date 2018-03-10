package ru.noties.history;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class HistoryStateBuilder<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> HistoryStateBuilder<K> create(@NonNull Class<K> type) {
        return new HistoryStateBuilder<>();
    }

    private final List<Entry<K>> entries = new ArrayList<>(3);

    @NonNull
    public HistoryStateBuilder<K> push(@NonNull K key) {
        entries.add(Entry.create(key).build());
        return this;
    }

    @NonNull
    public HistoryStateBuilder<K> push(@NonNull K key, @NonNull Parcelable state) {
        entries.add(Entry.create(key, state).build());
        return this;
    }

    @NonNull
    public HistoryStateBuilder<K> push(@NonNull EntryDef<K> entryDef) {
        entries.add(entryDef.build());
        return this;
    }

    @NonNull
    public HistoryStateBuilder<K> pushIf(boolean value, @NonNull K key) {
        if (value) {
            push(key);
        }
        return this;
    }

    @NonNull
    public HistoryStateBuilder<K> pushIf(boolean value, @NonNull K key, @NonNull Parcelable state) {
        if (value) {
            push(key, state);
        }
        return this;
    }

    @NonNull
    public HistoryStateBuilder<K> pushIf(boolean value, @NonNull EntryDef<K> entryDef) {
        if (value) {
            push(entryDef);
        }
        return this;
    }

    @NonNull
    public HistoryState build() {
        //noinspection unchecked
        return new HistoryState((List) entries);
    }
}
