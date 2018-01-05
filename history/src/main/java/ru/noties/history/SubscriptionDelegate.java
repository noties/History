package ru.noties.history;

import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class SubscriptionDelegate implements Subscription, Subscription.Visitor {

    private Subscription subscription;

    @Override
    public void visit(@NonNull Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    @NonNull
    @Override
    public Subscription accept(@NonNull Visitor visitor) {
        visitor.visit(this);
        return this;
    }

    public boolean hasSubscription() {
        return subscription != null;
    }
}
