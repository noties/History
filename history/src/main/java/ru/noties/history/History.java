package ru.noties.history;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class History<K extends Enum<K>> {

    /**
     * Factory method to create a new instance of {@link History}
     *
     * @param keyType enum type of keys
     * @return a new instance of {@link History}
     */
    @NonNull
    public static <K extends Enum<K>> History<K> create(@NonNull Class<K> keyType) {
        return new HistoryImpl<>();
    }

    /**
     * Factory method to create an instance of {@link HistoryStateBuilder}. Please note as {@link History}
     * requires no configuration this stateBuilder is used to create {@link HistoryState} only. When
     * called {@link HistoryStateBuilder#build()} the returned object is {@link HistoryState} so it
     * can be further passed to a <em>configured</em> {@link History} instance (registered observers).
     *
     * @param keyType enum type of keys
     * @return new instance of {@link HistoryStateBuilder}
     */
    @NonNull
    public static <K extends Enum<K>> HistoryStateBuilder<K> stateBuilder(@NonNull Class<K> keyType) {
        return new HistoryStateBuilder<>();
    }

    /**
     * Interface to listen for history changes
     *
     * @see #observe(Observer)
     */
    public interface Observer<K extends Enum<K>> {

        /**
         * @param previous {@link Entry} or null if `current` is the first one
         * @param current  {@link Entry} which becomes active
         * @see #push(EntryDef)
         */
        void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current);

        /**
         * @param previous {@link Entry} or null if `current` is the first one
         * @param current  {@link Entry} which replaces (if any) `previous` and becomes active,
         *                 `previous` is removed from history
         * @see #replace(EntryDef)
         */
        void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current);

        /**
         * @param popped   {@link Entry} which is popped
         * @param toAppear {@link Entry} to become active (previous in history), but only if it\'s
         *                 present, null otherwise
         * @see #pop()
         */
        void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear);

        /**
         * @param popped   {@link Entry} list of popped items
         * @param toAppear {@link Entry} to become active (previous in history), but only if it\'s
         *                 present, null otherwise
         * @see #popTo(Entry)
         */
        void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear);

        /**
         * @param dropped a list of inactive entries that are dropped
         * @see #drop(Entry)
         * @see #drop(Collection)
         * @see #drop(Filter)
         */
        void onEntriesDropped(@NonNull List<Entry<K>> dropped);
    }

    /**
     * Interface to be used if {@link #save(Filter)} requires filtering of existing entries or to
     * determine which {@link Entry}\'s should be removed in {@link #drop(Filter)} operation
     */
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

    /**
     * @param entry {@link Entry} to pop to
     * @return a list of popped entries
     */
    @NonNull
    public abstract List<Entry<K>> popTo(@NonNull Entry<K> entry);

    /**
     * @param entry {@link Entry} to remove from this history instance
     * @return a flag indicating if operation is successful
     * @throws IllegalStateException if requested entry is currently active one (last)
     * @see #drop(Collection)
     * @see #drop(Filter)
     */
    public abstract boolean drop(@NonNull Entry<K> entry) throws IllegalStateException;

    /**
     * @param entries a collection of {@link Entry} to remove from this history instance
     * @return a flag indicating if operation is successful
     * @throws IllegalStateException if any of the entries in supplied collection is active one (last)
     * @see #drop(Entry)
     * @see #drop(Filter)
     */
    public abstract boolean drop(@NonNull Collection<Entry<K>> entries) throws IllegalStateException;

    /**
     * Note that unlike {@link #drop(Entry)} and {@link #drop(Collection)} this method doesn\'t throw
     * an exception as filter won\'t be called on an active (last) entry.
     *
     * @param filter {@link Filter} to determine which {@link Entry}\'s should be removed from
     *               this history instance
     * @return a flag indicating if operation is successful
     * @see #drop(Entry)
     * @see #drop(Collection)
     */
    public abstract boolean drop(@NonNull Filter<K> filter);

    /**
     * @return first {@link Entry} in this history if it\'s present (history not empty)
     */
    @Nullable
    public abstract Entry<K> first();

    /**
     * @return last {@link Entry} in this history if it\'s present (history not empty)
     */
    @Nullable
    public abstract Entry<K> last();

    /**
     * @return a list of {@link Entry} that this history holds
     */
    @NonNull
    public abstract List<Entry<K>> entries();

    /**
     * @param key to search for
     * @return first {@link Entry} in this history with provided `key` or null if it\'s none
     */
    @Nullable
    public abstract Entry<K> first(@NonNull K key);

    /**
     * @param key to search for
     * @return last {@link Entry} in this history with provided `key` or null if it\'s none
     */
    @Nullable
    public abstract Entry<K> last(@NonNull K key);

    /**
     * @param key to search for
     * @return a list of {@link Entry} with specified `key` or empty list if there are none
     */
    @NonNull
    public abstract List<Entry<K>> entries(@NonNull K key);

    /**
     * @return number of {@link Entry} that this {@link History} holds
     */
    public abstract int length();

    /**
     * @param index at which requested {@link Entry} positioned
     * @return {@link Entry}
     * @throws IndexOutOfBoundsException if provided index is out of this history range
     */
    @SuppressWarnings("SameParameterValue")
    @NonNull
    public abstract Entry<K> entryAt(int index) throws IndexOutOfBoundsException;

    /**
     * Registers {@link Observer} to be notified about {@link History} modifications
     *
     * @param observer {@link Observer} to register
     * @return {@link Subscription}
     */
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

    /**
     * @param historyState {@link HistoryState} to restore from
     * @return a flag indicating if state was restored (now {@link History} is not empty)
     * @throws IllegalStateException if current {@link History} is not empty
     * @see HistoryState#restore(Bundle, String)
     * @see HistoryState#restore(Parcelable)
     */
    public abstract boolean restore(@Nullable HistoryState historyState) throws IllegalStateException;

    /**
     * @return a flag indicating if clear operation modified history (false will be
     * returned if history is already empty)
     */
    public abstract boolean clear();
}
