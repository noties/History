package ru.noties.screen;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.history.Subscription;
import ru.noties.screen.plugin.Plugin;

@SuppressWarnings("unused")
public abstract class ScreenManager<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder() {
        return new ScreenManagerBuilder<>();
    }

    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder(
            @NonNull History<K> history,
            @NonNull ScreenProvider<K> screenProvider
    ) {
        return new ScreenManagerBuilder<K>()
                .history(history)
                .screenProvider(screenProvider);
    }

    @NonNull
    public abstract Activity activity();

    @NonNull
    public abstract History<K> history();

    @NonNull
    public abstract ViewGroup container();

    /**
     * Get associated screen with View provided. Can return null if no screen has specified view.
     *
     * @param view of a {@link Screen} to find
     * @return {@link Screen} or null
     */
    @Nullable
    public abstract Screen<K, ? extends Parcelable> findScreen(@NonNull View view);

    @Nullable
    public abstract Screen<K, ? extends Parcelable> findScreen(@NonNull Entry<K> entry);

    /**
     * @param screen {@link Screen} for which to resolve visibility
     * @return {@link Visibility} of supplied {@link Screen} or null if screen is not attached
     */
    @Nullable
    public abstract Visibility screenVisibility(@NonNull Screen<K, ? extends Parcelable> screen);


    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract Subscription screenCallbacks(@NonNull ScreenLifecycleCallbacks<K> screenLifecycleCallbacks);

    @NonNull
    public abstract ScreenLifecycle screenLifecycle(@NonNull Screen<K, ? extends Parcelable> screen);


    public abstract boolean restoreState(@Nullable HistoryState state);

    @NonNull
    public abstract <P extends Plugin> P plugin(@NonNull Class<P> plugin) throws IllegalStateException;

    /**
     * @return a boolean indicating if there is currently a {@link ru.noties.screen.change.Change}
     * running (transition between screens)
     */
    public abstract boolean isChangingScreens();
}
