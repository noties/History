package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.noties.debug.Debug;
import ru.noties.history.Entry;
import ru.noties.history.History;

class LoggingObserver<K extends Enum<K>> implements History.Observer<K> {

    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        Debug.i("previous: %s, current: %s", previous, current);
    }

    @Override
    public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        Debug.i("previous: %s, current: %s", previous, current);
    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {
        Debug.i("popped: %s, toAppear: %s", popped, toAppear);
    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {
        Debug.i("popped: %s, toAppear: %s", popped, toAppear);
    }

    @Override
    public void onEntriesDropped(@NonNull List<Entry<K>> dropped) {
        Debug.i("dropped: %s", dropped);
    }
}
