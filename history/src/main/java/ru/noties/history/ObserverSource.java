package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.listeners.Listeners;

class ObserverSource<K extends Enum<K>> implements History.Observer<K> {

    private final Listeners<History.Observer<K>> observers = Listeners.create(3);

    @NonNull
    Subscription add(@NonNull History.Observer<K> observer) {
        return new SubscriptionImpl<>(observers, observer);
    }

    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        for (History.Observer<K> observer : observers.begin()) {
            observer.onEntryPushed(previous, current);
        }
    }

    @Override
    public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        for (History.Observer<K> observer : observers.begin()) {
            observer.onEntryReplaced(previous, current);
        }
    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {
        for (History.Observer<K> observer : observers.begin()) {
            observer.onEntryPopped(popped, toAppear);
        }
    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {
        for (History.Observer<K> observer : observers.begin()) {
            observer.onEntriesPopped(popped, toAppear);
        }
    }
}
