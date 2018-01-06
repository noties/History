package ru.noties.history.screen.plugin;

import android.support.annotation.NonNull;

import ru.noties.history.Subscription;
import ru.noties.history.SubscriptionImpl;
import ru.noties.listeners.Listeners;

public abstract class PermissionResultPlugin implements Plugin {

    public interface Action {

        boolean onRequestPermissionsResult(
                int requestCode,
                @NonNull String[] permissions,
                @NonNull int[] grantResults
        );
    }

    @NonNull
    public static PermissionResultPlugin create() {
        return new Impl();
    }

    @NonNull
    public abstract Subscription observe(int requestCode, @NonNull Action action);

    @NonNull
    public abstract Subscription observeAll(@NonNull Action action);


    public abstract boolean onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    );

    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return PermissionResultPlugin.class;
    }

    private static class Impl extends PermissionResultPlugin {

        private final Listeners<Item> listeners = Listeners.create(3);

        @NonNull
        @Override
        public Subscription observe(int requestCode, @NonNull Action action) {
            return new SubscriptionImpl<>(listeners, new Item(requestCode, action));
        }

        @NonNull
        @Override
        public Subscription observeAll(@NonNull Action action) {
            return new SubscriptionImpl<>(listeners, new Item(-1, action));
        }

        @Override
        public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            boolean result = false;
            for (Item item : listeners.begin()) {
                if (item.requestCode == -1
                        || item.requestCode == requestCode) {
                    result |= item.action.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
            return result;
        }

        private static class Item {

            final int requestCode;
            final Action action;

            Item(int requestCode, @NonNull Action action) {
                this.requestCode = requestCode;
                this.action = action;
            }
        }
    }
}
