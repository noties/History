package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.history.Entry;

/**
 * @see ScreenManagerBuilder#visibilityProvider(VisibilityProvider)
 */
@SuppressWarnings("WeakerAccess")
public abstract class VisibilityProvider<K extends Enum<K>> {

    /**
     * Create default {@link VisibilityProvider} that uses provided {@link Visibility} for all the
     * inactive screens (use `null` for a screen to be detached)
     *
     * @param visibility {@link Visibility} to be used for inactive screen (or `null` if screen must be detached)
     * @return {@link VisibilityProvider}
     */
    @NonNull
    public static <K extends Enum<K>> VisibilityProvider<K> create(@Nullable Visibility visibility) {
        return new Impl<>(visibility);
    }

    /**
     * Factory method to obtain an instance of {@link VisibilityProviderBuilder}
     *
     * @param type of the key
     * @return {@link VisibilityProviderBuilder}
     */
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
