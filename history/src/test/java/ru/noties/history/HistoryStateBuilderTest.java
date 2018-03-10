package ru.noties.history;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HistoryStateBuilderTest {

    enum Key {
        FIRST
    }

    private HistoryStateBuilder<Key> stateBuilder;

    @Before
    public void before() {
        stateBuilder = HistoryStateBuilder.create(Key.class);
    }

    @Test
    public void empty_state_does_not_restore() {

        final History<Key> history = History.create(Key.class);

        assertFalse(history.restore(stateBuilder.build()));
    }

    @Test
    public void empty() {
        assertSize(0, stateBuilder.build());
    }

    @Test
    public void single_push() {
        stateBuilder.push(Key.FIRST);
        assertSize(1, stateBuilder.build());
    }

    @Test
    public void single_push_state() {
        stateBuilder.push(Key.FIRST, EmptyState.instance());
        assertSize(1, stateBuilder.build());
    }

    @Test
    public void single_push_entry() {
        stateBuilder.push(Entry.create(Key.FIRST));
        assertSize(1, stateBuilder.build());
    }

    @Test
    public void push_if_false() {
        stateBuilder.pushIf(false, Key.FIRST);
        assertSize(0, stateBuilder.build());
    }

    @Test
    public void push_if_false_state() {
        stateBuilder.pushIf(false, Key.FIRST, EmptyState.instance());
        assertSize(0, stateBuilder.build());
    }

    @Test
    public void push_if_false_entry() {
        stateBuilder.pushIf(false, Entry.create(Key.FIRST));
        assertSize(0, stateBuilder.build());
    }

    @Test
    public void push_if_true() {
        stateBuilder.pushIf(true, Key.FIRST);
        assertSize(1, stateBuilder.build());
    }

    @Test
    public void push_if_true_state() {
        stateBuilder.pushIf(true, Key.FIRST, EmptyState.instance());
        assertSize(1, stateBuilder.build());
    }

    @Test
    public void push_if_true_entry() {
        stateBuilder.pushIf(true, Entry.create(Key.FIRST));
        assertSize(1, stateBuilder.build());
    }

    private void assertSize(int expected, @NonNull HistoryState state) {
        //noinspection unchecked
        assertEquals(expected, state.entries().size());
    }
}
