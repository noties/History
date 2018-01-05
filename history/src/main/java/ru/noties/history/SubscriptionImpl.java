package ru.noties.history;

import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

public class SubscriptionImpl<T> implements Subscription {

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
