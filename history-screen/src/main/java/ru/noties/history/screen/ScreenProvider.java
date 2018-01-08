package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * @see ScreenManagerBuilder#screenProvider(ScreenProvider)
 */
public abstract class ScreenProvider<K extends Enum<K>> {

    /**
     * Factory method to obtain {@link ScreenProviderBuilder} instance
     *
     * @param type of the key (used for type inference and additional validation that all enum constants
     *             have registered {@link Screen}
     * @return an instance of {@link ScreenProviderBuilder}
     */
    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> ScreenProviderBuilder<K> builder(@NonNull Class<K> type) {
        return new ScreenProviderBuilder<>(type);
    }

    @NonNull
    public abstract <S extends Parcelable> Screen<K, ? extends Parcelable> provide(@NonNull K key, @NonNull S state);
}
