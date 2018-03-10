package ru.noties.history;

import android.support.annotation.NonNull;

/**
 * @see SubscriptionImpl
 * @see CompositeSubscription
 * @see SubscriptionHolder
 */
public interface Subscription {

    interface Visitor {
        void visit(@NonNull Subscription subscription);
    }

    /**
     * Unsubscribe current instance
     */
    void unsubscribe();

    /**
     * @param visitor {@link Visitor} to visit this instance
     * @return self for chaining
     */
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @NonNull
    Subscription accept(@NonNull Visitor visitor);
}
