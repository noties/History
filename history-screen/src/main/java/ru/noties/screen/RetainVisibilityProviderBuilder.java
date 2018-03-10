package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.noties.history.Entry;

@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class RetainVisibilityProviderBuilder<K extends Enum<K>> {

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> RetainVisibilityProviderBuilder<K> create(@NonNull Class<K> type) {
        return new RetainVisibilityProviderBuilder<>();
    }

    /**
     * Tags exact pair of entries. Provided {@link RetainVisibility} will only be used when `K toResolve`
     * become inactive and `K active` becomes active
     *
     * @param toResolve  enum key of inactive entry
     * @param active     enum key of active entry
     * @param retainVisibility {@link RetainVisibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public RetainVisibilityProviderBuilder<K> when(@NonNull K toResolve, @NonNull K active, @Nullable RetainVisibility retainVisibility) {
        final Item<K> item = new Item<>(toResolve, active);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified pair of keys (%s - %s) already have " +
                    "defaultVisibility defined", toResolve, active));
        }
        map.put(item, retainVisibility);
        return this;
    }

    /**
     * Tags active Entry, so when it is going to appear, any item that becomes inactive will have returned here
     * RetainVisibility.
     *
     * @param active     enum key for active entry
     * @param retainVisibility {@link RetainVisibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public RetainVisibilityProviderBuilder<K> whenTo(@NonNull K active, @Nullable RetainVisibility retainVisibility) {
        final Item<K> item = new Item<>(null, active);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified active key (%s) already has " +
                    "defaultVisibility defined", active));
        }
        map.put(item, retainVisibility);
        return this;
    }

    /**
     * Tags inactive entry, so no matter what active Entry will be inactive Entry will always have
     * returned here RetainVisibility
     *
     * @param toResolve  enum key for inactive entry
     * @param retainVisibility {@link RetainVisibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public RetainVisibilityProviderBuilder<K> whenFrom(@NonNull K toResolve, @Nullable RetainVisibility retainVisibility) {
        final Item<K> item = new Item<>(toResolve, null);
        if (map.containsKey(item)) {
            throw new IllegalStateException(String.format("Specified toResolve key (%s) already has " +
                    "defaultVisibility defined", toResolve));
        }
        map.put(item, retainVisibility);
        return this;
    }

    /**
     * Sets default RetainVisibility to apply if {@link ScreenManager} encounters unknown rule for
     * inactive entry defaultVisibility (provided to this stateBuilder). Defaults to `null`, so inactive entry
     * will be detached
     *
     * @param retainVisibility {@link RetainVisibility} to apply to inactive entry
     * @return self for chaining
     */
    @NonNull
    public RetainVisibilityProviderBuilder<K> defaultVisibility(@Nullable RetainVisibility retainVisibility) {
        this.defaultVisibility = retainVisibility;
        return this;
    }

    @NonNull
    public RetainVisibilityProvider<K> build() {
        if (isBuilt) {
            throw new IllegalStateException("This instance of RetainVisibilityProviderBuilder has " +
                    "already been built");
        }
        isBuilt = true;
        return new Impl<>(map, defaultVisibility);
    }


    private final Map<Item<K>, RetainVisibility> map = new HashMap<>(3);

    private RetainVisibility defaultVisibility;

    private boolean isBuilt;

    RetainVisibilityProviderBuilder() {
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

    private static class Impl<K extends Enum<K>> extends RetainVisibilityProvider<K> {

        private final Map<Item<K>, RetainVisibility> map;
        private final RetainVisibility defaultVisibility;
        private final Item<K> item;

        Impl(@NonNull Map<Item<K>, RetainVisibility> map, @Nullable RetainVisibility defaultVisibility) {
            this.map = map;
            this.defaultVisibility = defaultVisibility;
            this.item = new Item<>(null, null);
        }

        @Nullable
        @Override
        public RetainVisibility resolveRetainVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active) {

            RetainVisibility visibility;

            visibility = map.get(item.set(toResolve.key(), active.key()));
            if (visibility != null) {
                return visibility;
            }

            visibility = map.get(item.set(null, active.key()));
            if (visibility != null) {
                return visibility;
            }

            visibility = map.get(item.set(toResolve.key(), null));
            if (visibility != null) {
                return visibility;
            }

            return defaultVisibility;
        }
    }
}
