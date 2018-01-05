package ru.noties.history.screen;

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
import ru.noties.history.screen.plugin.Plugin;

public abstract class ScreenManager<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder() {
        return new ScreenManagerBuilder<>();
    }

    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder(@NonNull History<K> history, @NonNull ScreenProvider<K> screenProvider) {
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


    // todo: isRestoring ?
    // todo: clear ?

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract Subscription screenCallbacks(@NonNull ScreenLifecycleCallbacks<K> screenLifecycleCallbacks);

    @NonNull
    public abstract ScreenLifecycle screenLifecycle(@NonNull Screen<K, ? extends Parcelable> screen);

    public abstract boolean restoreState(@Nullable HistoryState state);

    @NonNull
    public abstract <P extends Plugin> P plugin(@NonNull Class<P> plugin) throws IllegalStateException;
}
