package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.noties.history.Entry;

@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class VisibilityProviderBuilder<K extends Enum<K>> {

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> VisibilityProviderBuilder<K> create(@NonNull Class<K> type) {
        return new VisibilityProviderBuilder<>();
    }

    /**
     * Tags exact pair of entries. Provided {@link Visibility} will only be used when `K toResolve`
     * become inactive and `K active` becomes active
     *
     * @param toResolve  enum key of inactive entry
     * @param active     enum key of active entry
     * @param visibility {@link Visibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public VisibilityProviderBuilder<K> when(@NonNull K toResolve, @NonNull K active, @Nullable Visibility visibility) {
        final Item<K> item = new Item<>(toResolve, active);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified pair of keys (%s - %s) already have " +
                    "visibility defined", toResolve, active));
        }
        map.put(item, visibility);
        return this;
    }

    /**
     * Tags active Entry, so when it is going to appear, any item that becomes inactive will have returned here
     * Visibility.
     *
     * @param active     enum key for active entry
     * @param visibility {@link Visibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public VisibilityProviderBuilder<K> whenTo(@NonNull K active, @Nullable Visibility visibility) {
        final Item<K> item = new Item<>(null, active);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified active key (%s) already has " +
                    "visibility defined", active));
        }
        map.put(item, visibility);
        return this;
    }

    /**
     * Tags inactive entry, so no matter what active Entry will be inactive Entry will always have
     * returned here Visibility
     *
     * @param toResolve  enum key for inactive entry
     * @param visibility {@link Visibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public VisibilityProviderBuilder<K> whenFrom(@NonNull K toResolve, @Nullable Visibility visibility) {
        final Item<K> item = new Item<>(toResolve, null);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified toResolve key (%s) already has " +
                    "visibility defined", toResolve));
        }
        map.put(item, visibility);
        return this;
    }

    /**
     * Sets default Visibility to apply if {@link ScreenManager} encounters unknown rule for
     * inactive entry visibility (provided to this builder). Defaults to `null`, so inactive entry
     * will be detached
     *
     * @param visibility {@link Visibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public VisibilityProviderBuilder<K> defaultVisibility(@Nullable Visibility visibility) {
        this.defaultVisibility = visibility;
        return this;
    }

    @NonNull
    public VisibilityProvider<K> build() {
        if (isBuilt) {
            throw new IllegalStateException("This instance of VisibilityProviderBuilder has " +
                    "already been built");
        }
        isBuilt = true;
        return new Impl<>(map, defaultVisibility);
    }


    private final Map<Item<K>, Visibility> map = new HashMap<>(3);

    private Visibility defaultVisibility;

    private boolean isBuilt;

    VisibilityProviderBuilder() {
    }

    private static class Item<K extends Enum<K>> {

        K from;
        K to;

        Item(K from, K to) {
            this.from = from;
            this.to = to;
        }

        Item<K> set(K from, K to) {
            this.from = from;
            this.to = to;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item<?> item = (Item<?>) o;

            //noinspection SimplifiableIfStatement
            if (from != null ? !from.equals(item.from) : item.from != null) return false;
            return to != null ? to.equals(item.to) : item.to == null;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

    private static class Impl<K extends Enum<K>> extends VisibilityProvider<K> {

        private final Map<Item<K>, Visibility> map;
        private final Visibility visibility;
        private final Item<K> item;

        Impl(@NonNull Map<Item<K>, Visibility> map, @Nullable Visibility visibility) {
            this.map = map;
            this.visibility = visibility;
            this.item = new Item<>(null, null);
        }

        @Nullable
        @Override
        public Visibility resolveInActiveVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active) {

            if (map.containsKey(item.set(toResolve.key(), active.key()))) {
                return map.get(item);
            }

            if (map.containsKey(item.set(null, active.key()))) {
                return map.get(item);
            }

            if (map.containsKey(item.set(toResolve.key(), null))) {
                return map.get(item);
            }

            return visibility;
        }
    }
}
