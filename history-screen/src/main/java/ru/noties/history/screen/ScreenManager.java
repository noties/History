package ru.noties.history.screen;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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


    // todo: getScreen(... what to specify as an argument
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
