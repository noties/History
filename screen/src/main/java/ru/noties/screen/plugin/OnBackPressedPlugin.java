package ru.noties.screen.plugin;

import android.support.annotation.NonNull;

import ru.noties.history.Subscription;
import ru.noties.history.SubscriptionImpl;
import ru.noties.listeners.Listeners;

@SuppressWarnings("unused")
public abstract class OnBackPressedPlugin implements Plugin {

    // LIFO

    public interface Action {
        boolean onBackPressed();
    }

    @NonNull
    public static OnBackPressedPlugin create() {
        return new Impl();
    }

    public abstract boolean onBackPressed();

    @NonNull
    public abstract Subscription observe(@NonNull Action action);


    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return OnBackPressedPlugin.class;
    }

    private static class Impl extends OnBackPressedPlugin {

        private final Listeners<Action> listeners = Listeners.create(3);

        @Override
        public boolean onBackPressed() {
            boolean result = false;
            for (Action action : listeners.beginReversed()) {
                if (action.onBackPressed()) {
                    result = true;
                    break;
                }
            }
            listeners.end();
            return result;
        }

        @NonNull
        @Override
        public Subscription observe(@NonNull Action action) {
            return new SubscriptionImpl<>(listeners, action);
        }
    }
}
