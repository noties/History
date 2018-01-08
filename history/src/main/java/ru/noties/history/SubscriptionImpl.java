package ru.noties.history;

import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

/**
 * Simple implementation of a {@link Subscription} that holds listeners in Listeners data structure
 */
public class SubscriptionImpl<T> implements Subscription {

    @NonNull
    public static <T> SubscriptionImpl<T> create(@NonNull Listeners<T> listeners, @NonNull T t) {
        return new SubscriptionImpl<>(listeners, t);
    }

    private Listeners<T> listeners;
    private T t;

    public SubscriptionImpl(@NonNull Listeners<T> listeners, @NonNull T t) {
        this.listeners = listeners;
        this.t = t;

        this.listeners.add(t);
    }

    @Override
    public void unsubscribe() {
        if (t != null && listeners != null) {
            listeners.remove(t);
            t = null;
            listeners = null;
        }
    }

    @NonNull
    @Override
    public Subscription accept(@NonNull Visitor visitor) {
        visitor.visit(this);
        return this;
    }
}
