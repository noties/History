package ru.noties.history;

import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

@SuppressWarnings("unused")
public class CompositeSubscription implements Subscription, Subscription.Visitor {

    @NonNull
    public static CompositeSubscription create() {
        return new CompositeSubscription();
    }

    private final Listeners<Subscription> subscriptions = Listeners.create(3);

    @Override
    public void visit(@NonNull Subscription subscription) {
        // append
        subscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        for (Subscription subscription : subscriptions.begin()) {
            subscription.unsubscribe();
        }
        subscriptions.clear();
    }

    @NonNull
    @Override
    public Subscription accept(@NonNull Visitor visitor) {
        visitor.visit(this);
        return this;
    }
}
