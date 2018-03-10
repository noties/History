package ru.noties.history;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EntryIdGeneratorImpl {

    private EntryIdGenerator generator;

    @Before
    public void before() {
        generator = EntryIdGenerator.create();
    }

    @Test
    public void sortable() {

        // each generated id must be greater than previous

        final int runs = 1000;

        long result = -1L;

        for (int i = 0; i < runs; i++) {
            final long id = generator.next();
            assertTrue(id > result);
            result = id;
        }
    }

    @Test
    public void unique() {

        // each generated id must be unique

        final int runs = 765;

        final Set<Long> set = new HashSet<>(runs);

        for (int i = 0; i < runs; i++) {
            assertTrue(set.add(generator.next()));
        }
    }
}
