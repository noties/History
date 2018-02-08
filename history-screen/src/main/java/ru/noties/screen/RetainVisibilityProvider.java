package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.history.Entry;

/**
 * @see ScreenManagerBuilder#retainVisibilityProvider(RetainVisibilityProvider)
 */
@SuppressWarnings("WeakerAccess")
public abstract class RetainVisibilityProvider<K extends Enum<K>> {

    /**
     * Create default {@link RetainVisibilityProvider} that uses provided {@link RetainVisibility} for all the
     * inactive screens (use `null` for a screen to be detached)
     *
     * @param retainVisibility {@link RetainVisibility} to be used for inactive screen (or `null` if screen must be detached)
     * @return {@link RetainVisibilityProvider}
     */
    @NonNull
    public static <K extends Enum<K>> RetainVisibilityProvider<K> create(@Nullable RetainVisibility retainVisibility) {
        return new Impl<>(retainVisibility);
    }

    /**
     * Factory method to obtain an instance of {@link RetainVisibilityProviderBuilder}
     *
     * @param type of the key
     * @return {@link RetainVisibilityProviderBuilder}
     */
    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> RetainVisibilityProviderBuilder<K> builder(@NonNull Class<K> type) {
        return RetainVisibilityProviderBuilder.create(type);
    }

    /**
     * @param toResolve {@link Entry} that becomes inactive
     * @param active    {@link Entry} that becomes active
     * @return {@link RetainVisibility} or null to detach inactive view
     */
    @Nullable
    public abstract RetainVisibility resolveRetainVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active);


    private static class Impl<K extends Enum<K>> extends RetainVisibilityProvider<K> {

        private final RetainVisibility visibility;

        private Impl(@Nullable RetainVisibility visibility) {
            this.visibility = visibility;
        }

        @Nullable
        @Override
        public RetainVisibility resolveRetainVisibility(@NonNull Entry<K> toResolve, @NonNull Entry<K> active) {
            return visibility;
        }
    }
}
