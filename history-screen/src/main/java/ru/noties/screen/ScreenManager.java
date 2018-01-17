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
import ru.noties.screen.transit.ScreenSwitch;

@SuppressWarnings("unused")
public abstract class ScreenManager<K extends Enum<K>> {

    // todo: layoutChanges() -> Subscription

    /**
     * Factory method to obtain {@link ScreenManagerBuilder} instance. Please note that
     * required dependencies are not provided. Prefer using: {@link #builder(History, ScreenProvider)}
     * method.
     *
     * @return an instance of {@link ScreenManagerBuilder}
     */
    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder() {
        return new ScreenManagerBuilder<>();
    }

    /**
     * Factory method to obtain {@link ScreenManagerBuilder} instance with all required dependencies.
     *
     * @param history        {@link History} to rely on
     * @param screenProvider {@link ScreenProvider} to match {@link Entry} and {@link Screen}
     * @return an instance of {@link ScreenManagerBuilder}
     */
    @NonNull
    public static <K extends Enum<K>> ScreenManagerBuilder<K> builder(
            @NonNull History<K> history,
            @NonNull ScreenProvider<K> screenProvider
    ) {
        return new ScreenManagerBuilder<K>()
                .history(history)
                .screenProvider(screenProvider);
    }

    /**
     * @return activity to which we are attached to
     */
    @NonNull
    public abstract Activity activity();

    /**
     * @return backing {@link History}
     */
    @NonNull
    public abstract History<K> history();

    /**
     * @return container in which {@link Screen} are displayed
     */
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

    /**
     * Get associated screen with Entry provided. Can return null if no screen has specified entry.
     *
     * @param entry {@link Entry} of a {@link Screen} to find
     * @return {@link Screen} or null
     */
    @Nullable
    public abstract Screen<K, ? extends Parcelable> findScreen(@NonNull Entry<K> entry);

    /**
     * @param screen {@link Screen} for which to resolve visibility
     * @return {@link Visibility} of supplied {@link Screen} or null if screen is not attached
     */
    @Nullable
    public abstract Visibility screenVisibility(@NonNull Screen<K, ? extends Parcelable> screen);


    /**
     * Observe changes when state is changed. Provides a way to watch for the state of all {@link Screen}.
     * These callbacks receive Screen events <strong>before</strong> Screen itself, which allows
     * slight modification of state (for example for dependency injection)
     *
     * @param screenLifecycleCallbacks {@link ScreenLifecycleCallbacks}
     * @return {@link Subscription}
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract Subscription screenCallbacks(@NonNull ScreenLifecycleCallbacks<K> screenLifecycleCallbacks);

    /**
     * Used to trigger a notification when certain {@link LifecycleEvent} has occurred.
     *
     * @param screen {@link Screen} to which to obtain {@link ScreenLifecycle}
     * @return {@link ScreenLifecycle} of specified {@link Screen}
     * @throws IllegalStateException if supplied screen is not related with this ScreenManager
     *                               (destroyed or was created not by this manager internally)
     * @see ScreenLifecycle#on(LifecycleEvent, ScreenLifecycle.Action)
     * @see #validateScreen(Screen)
     */
    @NonNull
    public abstract ScreenLifecycle screenLifecycle(@NonNull Screen<K, ? extends Parcelable> screen)
            throws IllegalStateException;


    /**
     * @param state {@link HistoryState} to restore state from
     * @return a flag indicating if state restoration affected state (screens added). If `false` is
     * returned, it means that ScreenManager is empty
     */
    public abstract boolean restoreState(@Nullable HistoryState state);

    /**
     * @param plugin type of the plugin {@link Plugin#pluginType()}
     * @return {@link Plugin} with requested type or throws if it\'s not registered
     * @throws IllegalStateException if requested plugin is not registered
     */
    @NonNull
    public abstract <P extends Plugin> P plugin(@NonNull Class<P> plugin) throws IllegalStateException;

    /**
     * @return a boolean indicating if there is currently a {@link ScreenSwitch}
     * running (transition between screens)
     */
    public abstract boolean isSwitchingScreens();

    /**
     * @param runnable that will be run when next screen change is finished
     */
    public abstract void onNextScreenSwitchFinished(@NonNull Runnable runnable);

    /**
     * @param screen {@link Screen} to validate that it is related with this ScreenManager (not
     *               destroyed and was created by this ScreenManager internally)
     * @return a boolean indicating if supplied {@link Screen} is related with this ScreenManager
     */
    public abstract boolean validateScreen(@NonNull Screen<K, ? extends Parcelable> screen);
}
