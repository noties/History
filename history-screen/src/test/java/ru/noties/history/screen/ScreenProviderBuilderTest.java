package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.history.EmptyState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ScreenProviderBuilderTest {

    enum Key {
        FIRST, SECOND, THIRD
    }

    private ScreenProviderBuilder<Key> builder;

    @Before
    public void before() {
        builder = ScreenProvider.builder(Key.class);
    }

    @Test
    public void register_same_key_twice_throws() {

        builder.register(Key.FIRST, NO_OP);

        try {
            builder.register(Key.FIRST, NO_OP);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void key_not_registered_throws() {

        builder.register(Key.FIRST, NO_OP);
        builder.register(Key.SECOND, NO_OP);

        try {
            builder.build();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void all_registered() {
        builder.register(Key.FIRST, NO_OP)
                .register(Key.SECOND, NO_OP)
                .register(Key.THIRD, NO_OP)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void correctly_provide() {

        final Screen<Key, Parcelable> first = mock(Screen.class);
        final Screen<Key, Parcelable> second = mock(Screen.class);
        final Screen<Key, Parcelable> third = mock(Screen.class);

        assertNotEquals(first, second);
        assertNotEquals(second, third);

        final class Action implements ScreenProviderBuilder.Action<Key, Parcelable> {

            private final Screen<Key, Parcelable> screen;

            Action(Screen<Key, Parcelable> screen) {
                this.screen = screen;
            }

            @NonNull
            @Override
            public Screen<Key, Parcelable> provide(@NonNull Key key, @NonNull Parcelable state) {
                return screen;
            }
        }

        final ScreenProvider<Key> provider = builder.register(Key.FIRST, new Action(first))
                .register(Key.SECOND, new Action(second))
                .register(Key.THIRD, new Action(third))
                .build();

        assertEquals(first, provider.provide(Key.FIRST, EmptyState.instance()));
        assertEquals(second, provider.provide(Key.SECOND, EmptyState.instance()));
        assertEquals(third, provider.provide(Key.THIRD, EmptyState.instance()));
    }

    private static final ScreenProviderBuilder.Action<Key, Parcelable> NO_OP = new ScreenProviderBuilder.Action<Key, Parcelable>() {
        @NonNull
        @Override
        public Screen<Key, Parcelable> provide(@NonNull Key key, @NonNull Parcelable state) {
            throw new RuntimeException();
        }
    };
}