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
import ru.noties.history.screen.change.ChangeController;
import ru.noties.history.screen.change.ChangeControllerNoOp;
import ru.noties.history.screen.plugin.Plugin;

@SuppressWarnings({"WeakerAccess", "unused"})
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
    public ScreenManagerBuilder<K> changeController(@NonNull ChangeController<K> changeController) {
        this.changeController = changeController;
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
    public ScreenManagerBuilder<K> changeLock(@NonNull ChangeLock changeLock) {
        this.changeLock = changeLock;
        return this;
    }

    @NonNull
    public ScreenManager<K> build(@NonNull Activity activity, @NonNull ViewGroup container) {

        checkState();

        isBuilt = true;

        checkNotNull(history, "History is required in order to build ScreenManager");
        checkNotNull(screenProvider, "ScreenProvider is required in order to build ScreenManager");

        final ChangeController<K> controller;
        if (changeController == null) {
            controller = ChangeControllerNoOp.create();
        } else {
            controller = changeController;
        }

        final VisibilityProvider<K> provider;
        if (visibilityProvider == null) {
            provider = VisibilityProvider.create(null);
        } else {
            provider = visibilityProvider;
        }

        final ChangeLock lock;
        if (changeLock == null) {
            lock = CHANGE_LOCK_NO_OP;
        } else {
            lock = changeLock;
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
    private ChangeController<K> changeController;
    private VisibilityProvider<K> visibilityProvider;
    private LayoutInflater layoutInflater;
    private boolean detachLastView;
    private final Map<Class<? extends Plugin>, Plugin> plugins = new HashMap<>(3);
    private ChangeLock changeLock;

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

    private static final ChangeLock CHANGE_LOCK_NO_OP = new ChangeLock() {
        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    };
}
