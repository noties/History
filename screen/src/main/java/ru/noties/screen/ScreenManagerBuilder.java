package ru.noties.screen;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.noties.history.History;
import ru.noties.screen.plugin.Plugin;
import ru.noties.screen.transit.SwitchController;
import ru.noties.screen.transit.SwitchEngineNoOp;
import ru.noties.screen.transit.SwitchLock;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ScreenManagerBuilder<K extends Enum<K>> {

    /**
     * Required argument.
     *
     * @param history {@link History}
     * @return self for chaining
     */
    @Required
    @NonNull
    public ScreenManagerBuilder<K> history(@NonNull History<K> history) {
        this.history = history;
        return this;
    }

    /**
     * Required argument.
     *
     * @param screenProvider {@link ScreenProvider}
     * @return self for chaining
     * @see ScreenProvider#builder(Class)
     */
    @Required
    @NonNull
    public ScreenManagerBuilder<K> screenProvider(@NonNull ScreenProvider<K> screenProvider) {
        this.screenProvider = screenProvider;
        return this;
    }

    @NonNull
    public ScreenManagerBuilder<K> switchController(@NonNull SwitchController<K> switchController) {
        this.switchController = switchController;
        return this;
    }

    /**
     * By default will detach all inactive screens
     *
     * @param visibilityProvider {@link VisibilityProvider}
     * @return self for chaining
     * @see VisibilityProvider#builder(Class)
     * @see VisibilityProvider#create(Visibility)
     */
    @NonNull
    public ScreenManagerBuilder<K> visibilityProvider(@NonNull VisibilityProvider<K> visibilityProvider) {
        this.visibilityProvider = visibilityProvider;
        return this;
    }

    /**
     * Sets specific LayoutInflater to be used when creating a {@link Screen} View. If not provided
     * {@code activity.getLayoutInflater()} will be used each time a view creation is required.
     *
     * @param inflater LayoutInflater to be used
     * @return self for chaining
     */
    @NonNull
    public ScreenManagerBuilder<K> layoutInflater(@NonNull LayoutInflater inflater) {
        this.layoutInflater = inflater;
        return this;
    }

    /**
     * By default does not detach last view
     *
     * @param detachLastView a flag indicating if last view in layout must be detached. Introduced
     *                       so there is no visual glitch when finishing activity (screen is destroyed
     *                       but kept in layout whilst activity animates self destroy)
     * @return self for chaining
     */
    @NonNull
    public ScreenManagerBuilder<K> detachLastView(boolean detachLastView) {
        this.detachLastView = detachLastView;
        return this;
    }

    /**
     * By default has no plugins registered
     *
     * @param plugin {@link Plugin} to register
     * @return self for chaining
     * @see ScreenManager#plugin(Class)
     * @see Plugin
     * @see ru.noties.screen.plugin.ActivityResultPlugin
     * @see ru.noties.screen.plugin.PermissionResultPlugin
     * @see ru.noties.screen.plugin.OnBackPressedPlugin
     */
    @NonNull
    public ScreenManagerBuilder<K> addPlugin(@NonNull Plugin plugin) {

        // we add check here as this call modifies internal Map, so built instance of ScreenManager
        // can be affected by operating on this builder instance

        checkState();

        final Class<? extends Plugin> type = plugin.pluginType();
        if (plugins.containsKey(type)) {
            throw new IllegalStateException(String.format("Provided plugin has type: `%s` that has " +
                    "been already added. Impl: `%s`", type.getName(), plugin.getClass().getName()));
        }

        plugins.put(type, plugin);

        return this;
    }

    /**
     * By default has no registered plugins
     *
     * @param plugins an array of {@link Plugin} to register
     * @return self for chaining
     */
    @NonNull
    public ScreenManagerBuilder<K> addPlugins(@NonNull Plugin... plugins) {
        for (Plugin plugin : plugins) {
            checkNotNull(plugin, "Cannot add null plugin");
            addPlugin(plugin);
        }
        return this;
    }

    /**
     * By default has no registered plugins
     *
     * @param plugins a collection of {@link Plugin} tp register
     * @return self for chaining
     * @see #addPlugin(Plugin)
     */
    @NonNull
    public ScreenManagerBuilder<K> addPlugins(@NonNull Collection<? extends Plugin> plugins) {
        for (Plugin plugin : plugins) {
            checkNotNull(plugin, "Collection of plugins cannot contain null values");
            addPlugin(plugin);
        }
        return this;
    }

    /**
     * By default has not change lock
     *
     * @param switchLock {@link SwitchLock}
     * @return self for chaining
     * @see ScreenLayout
     */
    @NonNull
    public ScreenManagerBuilder<K> switchLock(@NonNull SwitchLock switchLock) {
        this.switchLock = switchLock;
        return this;
    }

    @NonNull
    public ScreenManager<K> build(@NonNull Activity activity, @NonNull ViewGroup container) {

        checkState();

        isBuilt = true;

        checkNotNull(history, "History is required in order to build ScreenManager");
        checkNotNull(screenProvider, "ScreenProvider is required in order to build ScreenManager");

        final SwitchController<K> controller;
        if (switchController == null) {
            controller = SwitchController.create(SwitchEngineNoOp.<K>create());
        } else {
            controller = switchController;
        }

        final VisibilityProvider<K> provider;
        if (visibilityProvider == null) {
            provider = VisibilityProvider.create(null);
        } else {
            provider = visibilityProvider;
        }

        final SwitchLock lock;
        if (switchLock == null) {
            lock = CHANGE_LOCK_NO_OP;
        } else {
            lock = switchLock;
        }

        return new ScreenManagerImpl<>(
                activity,
                container,
                history,
                screenProvider,
                controller,
                provider,
                layoutInflater,
                detachLastView,
                plugins,
                lock
        );
    }

    /**
     * Indicates that certain argument is required in order to build {@link ScreenManagerBuilder}
     */
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    @interface Required {
    }


    private History<K> history;
    private ScreenProvider<K> screenProvider;
    private SwitchController<K> switchController;
    private VisibilityProvider<K> visibilityProvider;
    private LayoutInflater layoutInflater;
    private boolean detachLastView;
    private final Map<Class<? extends Plugin>, Plugin> plugins = new HashMap<>(3);
    private SwitchLock switchLock;

    private boolean isBuilt;

    ScreenManagerBuilder() {
    }

    private void checkState() {
        if (isBuilt) {
            throw new IllegalStateException("This instance of ScreenManagerBuilder has already been built");
        }
    }

    private static void checkNotNull(@Nullable Object o, @NonNull String ifNullMessage) {
        if (o == null) {
            throw new IllegalStateException(ifNullMessage);
        }
    }

    private static final SwitchLock CHANGE_LOCK_NO_OP = new SwitchLock() {
        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    };
}
