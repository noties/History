package ru.noties.history;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Intermediate class that holds information about {@link Entry}
 */
@SuppressWarnings("WeakerAccess")
public final class EntryDef<K extends Enum<K>> {

    private static final EntryIdGenerator ID_GENERATOR = EntryIdGenerator.create();

    private final K key;
    private final Parcelable state;

    private boolean isBuilt;

    public EntryDef(@NonNull K key, @NonNull Parcelable state) {
        this.key = key;
        this.state = state;
    }

    @NonNull
    Entry<K> build() throws IllegalStateException {

        if (isBuilt) {
            throw new IllegalStateException("Please do not reuse EntryDef instance");
        }
        isBuilt = true;

        return new Entry<>(ID_GENERATOR.next(), key, state);
    }
}
