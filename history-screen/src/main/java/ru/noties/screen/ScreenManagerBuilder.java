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
import ru.noties.screen.transition.TransitionLock;
import ru.noties.screen.transition.ScreenTransition;
import ru.noties.screen.transition.TransitionController;

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

    /**
     * By default there is no switch controller registered, so all Screen switches won\'t be animated
     *
     * @param transitionController {@link TransitionController}
     * @return self for chaining
     */
    @NonNull
    public ScreenManagerBuilder<K> transitionController(@NonNull TransitionController<K> transitionController) {
        this.transitionController = transitionController;
        return this;
    }

    /**
     * By default will detach all inactive screens
     *
     * @param retainVisibilityProvider {@link RetainVisibilityProvider}
     * @return self for chaining
     * @see RetainVisibilityProvider#builder(Class)
     * @see RetainVisibilityProvider#create(RetainVisibility)
     */
    @NonNull
    public ScreenManagerBuilder<K> retainVisibilityProvider(@NonNull RetainVisibilityProvider<K> retainVisibilityProvider) {
        this.retainVisibilityProvider = retainVisibilityProvider;
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
        // can be affected by operating on this stateBuilder instance

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
     * @param transitionLock {@link TransitionLock}
     * @return self for chaining
     * @see ScreenLayout
     */
    @NonNull
    public ScreenManagerBuilder<K> transitionLock(@NonNull TransitionLock transitionLock) {
        this.transitionLock = transitionLock;
        return this;
    }

    @NonNull
    public ScreenManager<K> build(@NonNull Activity activity, @NonNull ViewGroup container) {

        checkState();

        isBuilt = true;

        checkNotNull(history, "History is required in order to build ScreenManager");
        checkNotNull(screenProvider, "ScreenProvider is required in order to build ScreenManager");

        final TransitionController<K> controller;
        if (transitionController == null) {
            controller = TransitionController.create(ScreenTransition.<K>noOp());
        } else {
            controller = transitionController;
        }

        final RetainVisibilityProvider<K> provider;
        if (retainVisibilityProvider == null) {
            provider = RetainVisibilityProvider.create(null);
        } else {
            provider = retainVisibilityProvider;
        }

        final TransitionLock lock;
        if (transitionLock == null) {
            lock = CHANGE_LOCK_NO_OP;
        } else {
            lock = transitionLock;
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
    private TransitionController<K> transitionController;
    private RetainVisibilityProvider<K> retainVisibilityProvider;
    private LayoutInflater layoutInflater;
    private boolean detachLastView;
    private final Map<Class<? extends Plugin>, Plugin> plugins = new HashMap<>(3);
    private TransitionLock transitionLock;

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

    private static final TransitionLock CHANGE_LOCK_NO_OP = new TransitionLock() {
        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    };
}
