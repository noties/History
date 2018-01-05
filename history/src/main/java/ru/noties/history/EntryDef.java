package ru.noties.history;

import android.os.Parcelable;
import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public final class EntryDef<K extends Enum<K>> {

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

        return new Entry<>(key, state);
    }
}
