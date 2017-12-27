package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;

public abstract class ScreenProvider<K extends Enum<K>> {

    // Type is used for additional validation that all enum constants are registered
    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ScreenProviderBuilder<K> builder(@NonNull Class<K> type) {
        return new ScreenProviderBuilder<>(type);
    }

    @NonNull
    public abstract <S extends Parcelable> Screen<K, ? extends Parcelable> provide(@NonNull K key, @NonNull S state);
}
