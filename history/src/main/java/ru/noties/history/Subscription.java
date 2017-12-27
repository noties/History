package ru.noties.history;

import android.support.annotation.NonNull;

public interface Subscription {

    interface Visitor {
        void visit(@NonNull Subscription subscription);
    }

    void unsubscribe();

    void accept(@NonNull Visitor visitor);
}
