package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScreenProviderBuilder<K extends Enum<K>> {

    public interface Action<K extends Enum<K>, S extends Parcelable> {

        @NonNull
        Screen<K, S> provide(@NonNull K key, @NonNull S state);
    }

    private final Class<K> type;

    private Map<K, Action<K, Parcelable>> actions;

    ScreenProviderBuilder(@NonNull Class<K> type) {
        this.type = type;
        this.actions = new EnumMap<K, Action<K, Parcelable>>(type);
    }

    @NonNull
    public <S extends Parcelable> ScreenProviderBuilder<K> register(
            @NonNull K key,
            @NonNull Action<K, S> action
    ) throws IllegalStateException {

        //noinspection unchecked
        if (actions.put(key, (Action<K, Parcelable>) action) != null) {
            throw new IllegalStateException("Specified key `" + key + "` already has registered provider");
        }

        return this;
    }

    @NonNull
    public ScreenProvider<K> build() throws IllegalStateException {
        validate();
        // lock from possible further modification
        this.actions = Collections.unmodifiableMap(actions);
        return new Impl<>(actions);
    }

    private void validate() throws IllegalStateException {
        final K[] constants = type.getEnumConstants();
        if (constants.length != actions.size()) {
            final List<K> missing = new ArrayList<>(3);
            for (K key : constants) {
                if (!actions.containsKey(key)) {
                    missing.add(key);
                }
            }
            throw new IllegalStateException("Some keys have no registered providers: " + missing.toString());
        }
    }

    private static class Impl<K extends Enum<K>> extends ScreenProvider<K> {

        private final Map<K, Action<K, Parcelable>> map;

        Impl(@NonNull Map<K, Action<K, Parcelable>> map) {
            this.map = map;
        }

        @NonNull
        @Override
        public <S extends Parcelable> Screen<K, ? extends Parcelable> provide(@NonNull K key, @NonNull S state) {
            return map.get(key).provide(key, state);
        }
    }
}
