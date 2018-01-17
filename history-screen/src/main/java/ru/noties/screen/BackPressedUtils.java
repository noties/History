package ru.noties.screen;

import android.support.annotation.NonNull;

import ru.noties.screen.plugin.OnBackPressedPlugin;

@SuppressWarnings("unused")
public abstract class BackPressedUtils {

    /**
     * Evaluates common logic when dealing with {@link ScreenManager}. First checks if there is
     * currently a change between screens, then redirects event to {@link OnBackPressedPlugin},
     * (if it\'s registered via {@link ScreenManagerBuilder#addPlugin(ru.noties.screen.plugin.Plugin)},
     * then executes {@link ru.noties.history.History#pop} operation.
     *
     * @param manager {@link ScreenManager}
     * @return a boolean indicating if back pressed event was consumed
     */
    public static boolean onBackPressed(@NonNull ScreenManager<? extends Enum> manager) {

        final boolean result;

        if (manager.isSwitchingScreens()) {

            result = true;

        } else {

            OnBackPressedPlugin plugin;
            try {
                plugin = manager.plugin(OnBackPressedPlugin.class);
            } catch (IllegalStateException e) {
                plugin = null;
            }

            result = (plugin != null && plugin.onBackPressed())
                    || manager.history().pop();
        }
        return result;
    }

    private BackPressedUtils() {
    }
}
