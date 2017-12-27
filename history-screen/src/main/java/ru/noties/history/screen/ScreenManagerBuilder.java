package ru.noties.history.screen;

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
import ru.noties.history.screen.plugin.Plugin;
import ru.noties.history.screen.transition.TransitionController;
import ru.noties.history.screen.transition.TransitionNoOp;

public class ScreenManagerBuilder<K extends Enum<K>> {

    @Required
    @NonNull
    public ScreenManagerBuilder<K> history(@NonNull History<K> history) {
        this.history = history;
        return this;
    }

    @Required
    @NonNull
    public ScreenManagerBuilder<K> screenProvider(@NonNull ScreenProvider<K> screenProvider) {
        this.screenProvider = screenProvider;
        return this;
    }

    @NonNull
    public ScreenManagerBuilder<K> transitionController(@NonNull TransitionController<K> transitionController) {
        this.transitionController = transitionController;
        return this;
    }

    @NonNull
    public ScreenManagerBuilder<K> visibilityProvider(@NonNull VisibilityProvider<K> visibilityProvider) {
        this.visibilityProvider = visibilityProvider;
        return this;
    }

    @NonNull
    public ScreenManagerBuilder<K> layoutInflater(@NonNull LayoutInflater inflater) {
        this.layoutInflater = inflater;
        return this;
    }

    // by default FALSE
    @NonNull
    public ScreenManagerBuilder<K> detachLastView(boolean detachLastView) {
        this.detachLastView = detachLastView;
        return this;
    }

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

    @NonNull
    public ScreenManagerBuilder<K> addPlugins(@NonNull Collection<? extends Plugin> plugins) {
        for (Plugin plugin : plugins) {
            checkNotNull(plugin, "Collection of plugins cannot contain null values");
            addPlugin(plugin);
        }
        return this;
    }

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
            controller = TransitionController.create(TransitionNoOp.instance(), TransitionNoOp.instance());
        } else {
            controller = transitionController;
        }

        final VisibilityProvider<K> provider;
        if (visibilityProvider == null) {
            provider = VisibilityProvider.create(null);
        } else {
            provider = visibilityProvider;
        }

        final TransitionLock lock;
        if (transitionLock == null) {
            lock = TRANSITION_LOCK_NO_OP;
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
    private VisibilityProvider<K> visibilityProvider;
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

    private static final TransitionLock TRANSITION_LOCK_NO_OP = new TransitionLock() {
        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    };
}
