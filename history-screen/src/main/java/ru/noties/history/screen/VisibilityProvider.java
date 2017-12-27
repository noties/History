package ru.noties.history.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.history.Entry;

public abstract class VisibilityProvider<K extends Enum<K>> {

    // will use the same for all entries (null to detach)
    @NonNull
    public static <K extends Enum<K>> VisibilityProvider<K> create(@Nullable Visibility visibility) {
        return new Impl<>(visibility);
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> VisibilityProviderBuilder<K> builder(@NonNull Class<K> type) {
        return VisibilityProviderBuilder.create(type);
    }

    // return NULL to detach (will be reattached when becomes active again (if at all)
    @Nullable
    public abstract Visibility resolveInActiveVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active);


    private static class Impl<K extends Enum<K>> extends VisibilityProvider<K> {

        private final Visibility visibility;

        private Impl(@Nullable Visibility visibility) {
            this.visibility = visibility;
        }

        @Nullable
        @Override
        public Visibility resolveInActiveVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active) {
            return visibility;
        }
    }
}
