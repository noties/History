package ru.noties.history;

import android.support.annotation.NonNull;

public interface Subscription {

    interface Visitor {
        void visit(@NonNull Subscription subscription);
    }

    void unsubscribe();

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    Subscription accept(@NonNull Visitor visitor);
}
