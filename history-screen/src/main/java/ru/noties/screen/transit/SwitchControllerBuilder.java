package ru.noties.screen.transit;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public class SwitchControllerBuilder<K extends Enum<K>> {

    @NonNull
    public SwitchControllerBuilder<K> when(@NonNull K from, @NonNull K to, @NonNull SwitchEngine<K> switchEngine) {
        final Key<K> key = new Key<>(from, to);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified pair{from: %1$s.%2$s, to: %1$s.%3$s} already " +
                    "has registered SwitchEngine", from.getClass().getSimpleName(), from, to));
        }
        map.put(key, switchEngine);
        return this;
    }

    @NonNull
    public SwitchControllerBuilder<K> whenTo(@NonNull K to, @NonNull SwitchEngine<K> switchEngine) {
        final Key<K> key = new Key<>(null, to);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified to{%s.%s} already has " +
                    "registered SwitchEngine", to.getClass().getSimpleName(), to));
        }
        map.put(key, switchEngine);
        return this;
    }

    @NonNull
    public SwitchControllerBuilder<K> whenFrom(@NonNull K from, @NonNull SwitchEngine<K> switchEngine) {
        final Key<K> key = new Key<>(from, null);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified from{%s.%s} already has " +
                    "registered SwitchEngine", from.getClass().getSimpleName(), from));
        }
        map.put(key, switchEngine);
        return this;
    }

    @NonNull
    public SwitchControllerBuilder<K> defaultEngine(@NonNull SwitchEngine<K> switchEngine) {
        this.defaultEngine = switchEngine;
        return this;
    }

    @NonNull
    public SwitchController<K> build() {
        if (defaultEngine == null) {
            defaultEngine = SwitchEngineNoOp.create();
        }
        return new Impl<>(map, defaultEngine);
    }


    private final Map<Key<K>, SwitchEngine<K>> map = new HashMap<>(3);

    private SwitchEngine<K> defaultEngine;


    private static class Key<K extends Enum<K>> {

        K from;
        K to;

        Key(K from, K to) {
            this.from = from;
            this.to = to;
        }

        Key() {
        }

        @NonNull
        Key<K> set(K from, K to) {
            this.from = from;
            this.to = to;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key<?> key = (Key<?>) o;

            if (from != null ? !from.equals(key.from) : key.from != null) return false;
            return to != null ? to.equals(key.to) : key.to == null;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

    private static class Impl<K extends Enum<K>> extends SwitchController<K> {

        private final Map<Key<K>, SwitchEngine<K>> map;

        private final SwitchEngine<K> defaultEngine;

        private final Key<K> lookUpKey = new Key<>();

        Impl(@NonNull Map<Key<K>, SwitchEngine<K>> map, @NonNull SwitchEngine<K> defaultEngine) {
            this.map = map;
            this.defaultEngine = defaultEngine;
        }

        @Nullable
        @Override
        protected SwitchEngineCallback apply(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return find(from.key, to.key).apply(reverse, from, to, endAction);
        }

        @NonNull
        @Override
        public SwitchEngine<K> switchEngine(@NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
            return find(from.key, to.key);
        }

        @NonNull
        private SwitchEngine<K> find(@NonNull K from, @NonNull K to) {

            SwitchEngine<K> engine;

            engine = map.get(lookUpKey.set(from, to));
            if (engine != null) {
                return engine;
            }

            engine = map.get(lookUpKey.set(null, to));
            if (engine != null) {
                return engine;
            }

            engine = map.get(lookUpKey.set(from, null));
            if (engine != null) {
                return engine;
            }

            return defaultEngine;
        }
    }
}
