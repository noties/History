package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class History<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> History<K> create(@NonNull Class<K> keyType) {
        return new HistoryImpl<>();
    }

    public interface Observer<K extends Enum<K>> {

        void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current);

        void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current);

        void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear);

        void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear);
    }

    public interface Filter<K extends Enum<K>> {

        /**
         * @param entry to validate
         * @return true if provided entry should be emitted (included)
         */
        boolean test(@NonNull Entry<K> entry);
    }

    /**
     * Places supplied {@link Entry} on top of current one or becomes root if this history instance
     * currently has no entries.
     *
     * @param entry {@link Entry} to push
     * @return previous {@link Entry} or null if supplier {@link Entry} is the first one
     * @throws IllegalStateException if supplied {@link EntryDef} was already used in any history
     *                               operations (like push or replace)
     */
    @Nullable
    public abstract Entry<K> push(@NonNull EntryDef<K> entry) throws IllegalStateException;

    /**
     * Replaces currently active {@link Entry} if it\'s present or becomes root if this history instance
     * currently has no entries.
     *
     * @param entry {@link Entry} to replace current active one
     * @return {@link Entry} that was replaced or null if there are no entries.
     * @throws IllegalStateException if supplied {@link EntryDef} was already used in any history
     *                               operations (like push or replace)
     */
    @Nullable
    public abstract Entry<K> replace(@NonNull EntryDef<K> entry) throws IllegalStateException;

    /**
     * Executes popHistory operation - removes currently active {@link Entry} if it\'s present
     *
     * @return a boolean indicating if further popHistory operations are available, so if returned `false`
     * it means that root {@link Entry} was removed and this history is empty (in terms of `onBackPressed`
     * event in Activity, it means that it should call `finish()` if this method returns `false`)
     */
    public abstract boolean pop();

    // returns a list of popped entries
    @NonNull
    public abstract List<Entry<K>> popTo(@NonNull Entry<K> entry);

    @Nullable
    public abstract Entry<K> first();

    @Nullable
    public abstract Entry<K> last();

    @NonNull
    public abstract List<Entry<K>> entries();

    @Nullable
    public abstract Entry<K> first(@NonNull K key);

    @Nullable
    public abstract Entry<K> last(@NonNull K key);

    @NonNull
    public abstract List<Entry<K>> entries(@NonNull K key);


    public abstract int length();

    // throws an exception if there is no entry available at specified index
    @SuppressWarnings("SameParameterValue")
    @NonNull
    public abstract Entry<K> entryAt(int index) throws IndexOutOfBoundsException;

    @NonNull
    public abstract Subscription observe(@NonNull Observer<K> observer);

    /**
     * Saves current {@link History} state. All entries will be preserved. To filter out some entries
     * {@link #save(Filter)} can be used.
     *
     * @return {@link HistoryState}
     * @see #save(Filter)
     */
    @NonNull
    public abstract HistoryState save();

    /**
     * Saves current {@link History} state. Will preserve only entries that specified {@link Filter}
     * indicates to be saved.
     *
     * @param filter {@link Filter} to test entries
     * @return {@link HistoryState}
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public abstract HistoryState save(@NonNull Filter<K> filter);

    // if null, nothing will happen....
    // false will be returned if nothing was restored
    // throws if history is not empty
    public abstract boolean restore(@Nullable HistoryState historyState) throws IllegalStateException;

    public abstract boolean clear();
}
