package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class HistoryImpl<K extends Enum<K>> extends History<K> {

    private final List<Entry<K>> entries = new ArrayList<>(3);

    private final ObserverSource<K> observer = new ObserverSource<>();

    @Nullable
    @Override
    public Entry<K> push(@NonNull EntryDef<K> entry) {
        final Entry<K> previous = last();
        final Entry<K> current = entry.build();
        entries.add(current);
        observer.onEntryPushed(previous, current);
        return previous;
    }

    @Nullable
    @Override
    public Entry<K> replace(@NonNull EntryDef<K> entry) {
        final int length = length();
        final Entry<K> previous = length > 0
                ? entries.remove(length - 1)
                : null;
        final Entry<K> current = entry.build();
        entries.add(current);
        observer.onEntryReplaced(previous, current);
        return previous;
    }

    @Override
    public boolean pop() {

        final int length = length();

        final Entry<K> popped = length > 0
                ? entries.remove(length - 1)
                : null;

        final Entry<K> last = last();

        if (popped != null) {
            observer.onEntryPopped(popped, last);
        }

        return last != null;
    }

    @NonNull
    @Override
    public List<Entry<K>> popTo(@NonNull Entry<K> entry) {

        final List<Entry<K>> out;

        final int length = length();
        final int index = length > 0
                ? entries.indexOf(entry)
                : -1;

        if (index >= 0) {

            final int poppedSize = length - index - 1;

            // if popTo requested for currently last item, no notification should occur
            if (poppedSize == 0) {
                out = Collections.emptyList();
            } else {
                final List<Entry<K>> list = new ArrayList<>(length - index - 1);
                for (int i = length - 1; i > index; i--) {
                    list.add(entries.remove(entries.size() - 1));
                }
                out = Collections.unmodifiableList(list);
                observer.onEntriesPopped(list, last());
            }
        } else {
            out = Collections.emptyList();
        }

        return out;
    }

    @Nullable
    @Override
    public Entry<K> first() {
        return entries.size() > 0
                ? entries.get(0)
                : null;
    }

    @Nullable
    @Override
    public Entry<K> last() {
        final int length = entries.size();
        return length > 0
                ? entries.get(length - 1)
                : null;
    }

    @NonNull
    @Override
    public List<Entry<K>> entries() {
        final List<Entry<K>> out;
        if (entries.size() == 0) {
            out = Collections.emptyList();
        } else {
            out = Collections.unmodifiableList(entries);
        }
        return out;
    }

    @Nullable
    @Override
    public Entry<K> first(@NonNull K key) {

        Entry<K> out = null;

        for (Entry<K> entry : entries) {
            if (key == entry.key()) {
                out = entry;
                break;
            }
        }
        return out;
    }

    @Nullable
    @Override
    public Entry<K> last(@NonNull K key) {

        Entry<K> out = null;
        Entry<K> entry;

        for (int i = entries.size() - 1; i >= 0; i--) {
            entry = entries.get(i);
            if (key == entry.key()) {
                out = entry;
                break;
            }
        }

        return out;
    }

    @NonNull
    @Override
    public List<Entry<K>> entries(@NonNull K key) {

        List<Entry<K>> list = null;

        if (length() > 0) {
            list = new ArrayList<>(3);
            for (Entry<K> entry : entries) {
                if (key == entry.key()) {
                    list.add(entry);
                }
            }
            if (list.size() == 0) {
                list = null;
            }
        }

        return list != null
                ? Collections.unmodifiableList(list)
                : Collections.<Entry<K>>emptyList();
    }

    @Override
    public int length() {
        return entries.size();
    }

    @NonNull
    @Override
    public Entry<K> entryAt(int index) throws IndexOutOfBoundsException {
        if (index < 0
                || index >= length()) {
            throw new IndexOutOfBoundsException();
        }
        return entries.get(index);
    }

    @NonNull
    @Override
    public Subscription observe(@NonNull Observer<K> observer) {
        return this.observer.add(observer);
    }

    @NonNull
    @Override
    public HistoryState save() {
        return saveState(null);
    }

    @NonNull
    @Override
    public HistoryState save(@NonNull Filter<K> filter) {
        return saveState(filter);
    }

    @Override
    public boolean restore(@Nullable HistoryState historyState) throws IllegalStateException {

        if (length() > 0) {
            throw new IllegalStateException("Cannot restore History state if History is not empty");
        }

        if (historyState == null) {
            return false;
        }

        final List<Entry<K>> list = historyState.entries();
        for (Entry<K> entry : list) {
            push(Entry.create(entry.key(), entry.state()));
        }

        return length() > 0;
    }

    private HistoryState saveState(@Nullable Filter<K> filter) {

        final int length = length();

        final List<Entry<K>> entries;

        if (length == 0) {

            entries = Collections.emptyList();

        } else {

            if (filter == null) {
                //noinspection unchecked
                filter = NO_OP;
            }

            entries = new ArrayList<>(length);

            for (Entry<K> entry : entries()) {
                if (filter.test(entry)) {
                    entries.add(entry);
                }
            }
        }

        //noinspection unchecked
        return new HistoryState((List<Entry>) (List) entries);
    }

    private static final Filter NO_OP = new Filter() {
        @Override
        public boolean test(@NonNull Entry entry) {
            return true;
        }
    };
}
