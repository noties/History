package ru.noties.history;

import android.os.Parcelable;
import android.support.annotation.NonNull;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Entry<K extends Enum<K>> {

    /**
     * Creates an {@link Entry} with {@link EmptyState} (aka no state at all)
     *
     * @param key to identify this {@link Entry}
     * @return new instance of {@link Entry}
     * @see EmptyState
     */
    @NonNull
    public static <K extends Enum<K>> EntryDef<K> create(@NonNull K key) {
        return new EntryDef<>(key, EmptyState.instance());
    }

    /**
     * Creates an {@link Entry} with specified state
     *
     * @param key   to identify this {@link Entry}
     * @param state of this {@link Entry}
     * @return new instance of {@link Entry}
     */
    @NonNull
    public static <K extends Enum<K>> EntryDef<K> create(@NonNull K key, @NonNull Parcelable state) {
        return new EntryDef<>(key, state);
    }

    private final K key;
    private final Parcelable state;

    Entry(@NonNull K key, @NonNull Parcelable state) {
        this.key = key;
        this.state = state;
    }

    @NonNull
    public K key() {
        return key;
    }

    @NonNull
    public <P extends Parcelable> P state() {
        //noinspection unchecked
        return (P) state;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "key=" + key +
                ", state=" + state +
                '}';
    }
}
