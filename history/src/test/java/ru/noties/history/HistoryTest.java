package ru.noties.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HistoryTest {

    enum Key {
        FIRST,
        SECOND
    }

    private History<Key> history;

    @Before
    public void before() {
        history = History.create(Key.class);
    }

    @Test
    public void empty_instance_push() {
        // as we are empty, null should be returned
        assertNull(history.push(Entry.create(Key.FIRST)));
    }

    @Test
    public void empty_instance_replace() {
        assertNull(history.replace(Entry.create(Key.FIRST)));
    }

    @Test
    public void empty_instance_pop() {
        assertFalse(history.pop());
    }

    @Test
    public void empty_instance_popTo() {
        //noinspection ConstantConditions
        assertEquals(0, history.popTo(new Entry<Key>(1L, Key.FIRST, null)).size());
    }

    @Test
    public void empty_instance_first() {
        assertNull(history.first());
    }

    @Test
    public void empty_instance_last() {
        assertNull(history.last());
    }

    @Test
    public void empty_instance_entries() {
        assertEquals(0, history.entries().size());
    }

    @Test
    public void empty_instance_first_key() {
        assertNull(history.first(Key.FIRST));
    }

    @Test
    public void empty_instance_last_key() {
        assertNull(history.last(Key.FIRST));
    }

    @Test
    public void empty_instance_entries_key() {
        assertEquals(0, history.entries(Key.FIRST).size());
    }

    @Test
    public void empty_instance_length() {
        assertEquals(0, history.length());
    }

    @Test
    public void empty_instance_entryAt() {
        try {
            history.entryAt(0);
            assertTrue(false);
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void empty_instance_clear() {
        assertFalse(history.clear());
    }

    @Test
    public void entry_def_reuse_throws() {
        final EntryDef<Key> entryDef = Entry.create(Key.FIRST);
        history.push(entryDef);
        try {
            history.push(entryDef);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void entry_def_state_empty() {
        final EntryDef<Key> entryDef = Entry.create(Key.FIRST);
        final Entry<Key> entry = entryDef.build();
        assertTrue(entry.state() instanceof EmptyState);
        assertEquals(EmptyState.instance(), entry.state());
    }

    @Test
    public void pop_to_the_current_item_no_notification() {

        final Flag flag = new Flag();

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        final History.Observer<Key> observer = new ObserverAdapter<Key>() {
            @Override
            public void onEntriesPopped(@NonNull List<Entry<Key>> popped, @Nullable Entry<Key> toAppear) {

                if (flag.marked()) {
                    assertTrue("Must not be called the second time", false);
                    return;
                }

                assertEquals(1, popped.size());
                assertEquals(Key.SECOND, popped.get(0).key());

                //noinspection ConstantConditions
                assertEquals(Key.FIRST, toAppear.key());

                flag.mark();
            }
        };

        history.observe(observer);

        //noinspection ConstantConditions
        history.popTo(history.first());

        //noinspection ConstantConditions
        history.popTo(history.first());

        assertEquals(1, history.length());

        //noinspection ConstantConditions
        assertEquals(Key.FIRST, history.first().key());
    }

    @Test
    public void drop_active_throws() {

        history.push(Entry.create(Key.FIRST));

        final Entry<Key> active = history.last();

        assertNotNull(active);

        try {
            history.drop(active);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void drop_collection_with_active_throws() {

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        assertEquals(2, history.length());

        final List<Entry<Key>> entries = history.entries();
        assertEquals(2, entries.size());

        try {
            history.drop(entries);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void drop_filter_not_supplied_active() {

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        assertEquals(2, history.length());

        final Entry<Key> active = history.last();

        assertNotNull(active);

        final class Filter implements History.Filter<Key> {

            private boolean called;

            @Override
            public boolean test(@NonNull Entry<Key> entry) {
                called = true;
                assertNotEquals(entry, active);
                return false;
            }
        }
        final Filter filter = new Filter();

        final boolean result = history.drop(filter);

        assertTrue(filter.called);
        assertFalse(result);
        assertEquals(2, history.length());
    }

    @Test
    public void drop_single() {

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        final Entry<Key> first = history.first();
        final Entry<Key> last = history.last();

        assertNotNull(first);
        assertNotNull(last);

        assertEquals(2, history.length());

        final boolean result = history.drop(first);
        assertTrue(result);
        assertEquals(1, history.length());

        assertEquals(last, history.first());
    }

    @Test
    public void drop_collection() {

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));
        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        assertEquals(4, history.length());

        final List<Entry<Key>> list = history.entries(Key.FIRST);
        assertEquals(2, list.size());

        final boolean result = history.drop(list);

        assertTrue(result);
        assertEquals(2, history.length());

        for (Entry<Key> entry: history.entries()) {
            assertEquals(Key.SECOND, entry.key());
        }
    }

    @Test
    public void drop_filter() {

        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));
        history.push(Entry.create(Key.FIRST));
        history.push(Entry.create(Key.SECOND));

        assertEquals(4, history.length());

        final boolean result = history.drop(new History.Filter<Key>() {
            @Override
            public boolean test(@NonNull Entry<Key> entry) {
                return Key.FIRST == entry.key();
            }
        });

        assertTrue(result);
        assertEquals(2, history.length());

        for (Entry<Key> entry: history.entries()) {
            assertEquals(Key.SECOND, entry.key());
        }
    }

    private static abstract class ObserverAdapter<K extends Enum<K>> implements History.Observer<K> {

        @Override
        public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
            throw new RuntimeException();
        }

        @Override
        public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
            throw new RuntimeException();
        }

        @Override
        public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {
            throw new RuntimeException();
        }

        @Override
        public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {
            throw new RuntimeException();
        }

        @Override
        public void onEntriesDropped(@NonNull List<Entry<K>> dropped) {
            throw new RuntimeException();
        }
    }
}
