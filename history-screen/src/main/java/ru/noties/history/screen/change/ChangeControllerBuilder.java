package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ChangeControllerBuilder<K extends Enum<K>> {

    @NonNull
    public ChangeControllerBuilder<K> when(@NonNull K from, @NonNull K to, @NonNull Change<K> change) {
        final Key<K> key = new Key<>(from, to);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified pair{from: %s, to: %s} already " +
                    "has registered Change", from, to));
        }
        map.put(key, change);
        return this;
    }

    @NonNull
    public ChangeControllerBuilder<K> when(
            @NonNull K from,
            @NonNull K to,
            @NonNull SingleChange<K> fromChange,
            @NonNull SingleChange<K> toChange
    ) {
        return when(from, to, CombinedChange.create(fromChange, toChange));
    }

    @NonNull
    public ChangeControllerBuilder<K> whenFrom(@NonNull K from, @NonNull Change<K> change) {
        final Key<K> key = new Key<>(from, null);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified key: %s already has registered " +
                    "`from` Change", from));
        }
        map.put(key, change);
        return this;
    }

    @NonNull
    public ChangeControllerBuilder<K> whenFrom(
            @NonNull K from,
            @NonNull SingleChange<K> fromChange,
            @NonNull SingleChange<K> toChange
    ) {
        return whenFrom(from, CombinedChange.create(fromChange, toChange));
    }

    @NonNull
    public ChangeControllerBuilder<K> whenTo(@NonNull K to, @NonNull Change<K> change) {
        final Key<K> key = new Key<>(null, to);
        if (map.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified key: %s already has registered " +
                    "`to` Change", to));
        }
        map.put(key, change);
        return this;
    }

    @NonNull
    public ChangeControllerBuilder<K> whenTo(
            @NonNull K to,
            @NonNull SingleChange<K> fromChange,
            @NonNull SingleChange<K> toChange
    ) {
        return whenTo(to, CombinedChange.create(fromChange, toChange));
    }

    @NonNull
    public ChangeControllerBuilder<K> defaultChange(@NonNull Change<K> change) {
        this.defaultChange = change;
        return this;
    }

    @NonNull
    public ChangeControllerBuilder<K> defaultChange(@NonNull SingleChange<K> fromChange, @NonNull SingleChange<K> toChange) {
        return defaultChange(CombinedChange.create(fromChange, toChange));
    }

    @NonNull
    public ChangeController<K> build() {
        if (defaultChange == null) {
            defaultChange = ChangeNoOp.instance();
        }
        return new Impl<>(map, defaultChange);
    }

    private final Map<Key<K>, Change<K>> map = new HashMap<>(3);

    private Change<K> defaultChange;

    private static class Impl<K extends Enum<K>> extends ChangeController<K> {

        private final Map<Key<K>, Change<K>> map;
        private final Change<K> defaultChange;

        private final Key<K> lookupKey = new Key<>();

        Impl(@NonNull Map<Key<K>, Change<K>> map, @NonNull Change<K> defaultChange) {
            this.map = map;
            this.defaultChange = defaultChange;
        }

        @NonNull
        @Override
        public ChangeCallback forward(@NonNull ScreenManager<K> manager, @Nullable Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return from != null
                    ? find(from.key, to.key).apply(false, manager, from, to, endAction)
                    : ChangeCallbackNoOp.noOp(endAction);
        }

        @NonNull
        @Override
        public ChangeCallback back(@NonNull ScreenManager<K> manager, @NonNull Screen<K, ? extends Parcelable> from, @Nullable Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return to != null
                    ? find(to.key, from.key).apply(true, manager, from, to, endAction)
                    : ChangeCallbackNoOp.noOp(endAction);
        }

        @NonNull
        private Change<K> find(@NonNull K from, @NonNull K to) {

            Change<K> change = map.get(lookupKey.set(from, to));
            if (change != null) {
                return change;
            }

            change = map.get(lookupKey.set(null, to));
            if (change != null) {
                return change;
            }

            change = map.get(lookupKey.set(from, null));
            if (change != null) {
                return change;
            }

            return defaultChange;
        }
    }

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

            //noinspection SimplifiableIfStatement
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
}
