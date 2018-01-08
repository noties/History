package ru.noties.history;

import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

/**
 * Can be used when a number of {@link Subscription} must be combined for example
 * to be unsubscribed after certain action. To add a {@link Subscription} to an
 * instance of CompositeSubscription:
 * {@code getSubscription().accept(compositeSubscription);}
 *
 * @see #create()
 * @see Subscription
 */
@SuppressWarnings("unused")
public class CompositeSubscription implements Subscription, Subscription.Visitor {

    /**
     * Factory method to obtain a new instance of CompositeSubscription
     *
     * @return a new instance of CompositeSubscription
     */
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
