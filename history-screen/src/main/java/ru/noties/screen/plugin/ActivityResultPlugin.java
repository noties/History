package ru.noties.screen.plugin;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.history.Subscription;
import ru.noties.history.SubscriptionImpl;
import ru.noties.listeners.Listeners;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ActivityResultPlugin implements Plugin {

    public interface Action {
        boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    @NonNull
    public static ActivityResultPlugin create() {
        return new Impl();
    }

    @NonNull
    public abstract Subscription observeAll(@NonNull Action action);

    /**
     * Starts listening for specified `requestCode` only (unlike {@link #observeAll(Action)})
     *
     * @param requestCode to listen for result
     * @param action      {@link Action} to be notified when activity result event occurs
     * @return {@link Subscription} to control subscription
     */
    @SuppressWarnings("SameParameterValue")
    @NonNull
    public abstract Subscription observe(@IntRange(from = 0) int requestCode, @NonNull Action action);


    @SuppressWarnings("UnusedReturnValue")
    public abstract boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return ActivityResultPlugin.class;
    }

    private static class Impl extends ActivityResultPlugin {

        private final Listeners<Item> items = Listeners.create(3);

        @NonNull
        @Override
        public Subscription observeAll(@NonNull Action action) {
            return new SubscriptionImpl<>(items, new Item(-1, action));
        }

        @NonNull
        @Override
        public Subscription observe(@IntRange(from = 0) int requestCode, @NonNull Action action) {
            return new SubscriptionImpl<>(items, new Item(requestCode, action));
        }

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            boolean result = false;
            for (Item item : items.begin()) {
                if (item.requestCode == -1
                        || item.requestCode == requestCode) {
                    result |= item.action.onActivityResult(requestCode, resultCode, data);
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
