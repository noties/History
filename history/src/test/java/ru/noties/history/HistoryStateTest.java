package ru.noties.history;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HistoryStateTest {

    private enum Key {
        FIRST,
        SECOND,
        THIRD
    }

    @Test
    public void empty() {

        final HistoryState in = HistoryStateBuilder.create(Key.class).build();
        final HistoryState out = saveRestore(in);

        assertTrue(in != out);

        assertEquals(0, size(in));
        assertEquals(size(in), size(out));
    }

    @Test
    public void first() {

        final HistoryState in = HistoryStateBuilder.create(Key.class)
                .push(Key.FIRST)
                .build();

        final HistoryState out = saveRestore(in);

        assertTrue(in != out);

        assertEquals(1, size(in));
        assertEquals(size(in), size(out));

        assertEquals(Key.FIRST, keyAt(out, 0));
    }

    @Test
    public void first_second() {

        final HistoryState in = HistoryStateBuilder.create(Key.class)
                .push(Key.FIRST)
                .push(Key.SECOND)
                .build();

        final HistoryState out = saveRestore(in);

        assertTrue(in != out);

        assertEquals(2, size(in));
        assertEquals(size(in), size(out));

        assertEquals(Key.FIRST, keyAt(out, 0));
        assertEquals(Key.SECOND, keyAt(out, 1));
    }

    @Test
    public void first_second_third() {

        final HistoryState in = HistoryStateBuilder.create(Key.class)
                .push(Key.FIRST)
                .push(Key.SECOND)
                .push(Key.THIRD)
                .build();

        final HistoryState out = saveRestore(in);

        assertTrue(in != out);

        assertEquals(3, size(in));
        assertEquals(size(in), size(out));

        assertEquals(Key.FIRST, keyAt(out, 0));
        assertEquals(Key.SECOND, keyAt(out, 1));
        assertEquals(Key.THIRD, keyAt(out, 2));
    }

    @Test
    public void first_state() {

        final HistoryState in = HistoryStateBuilder.create(Key.class)
                .push(Key.FIRST, new FirstState(13))
                .build();

        final HistoryState out = saveRestore(in);

        assertTrue(in != out);

        assertEquals(1, size(in));
        assertEquals(size(in), size(out));
        assertEquals(Key.FIRST, keyAt(out, 0));

        final FirstState firstState = stateAt(out, 0);
        assertEquals(13, firstState.i);
    }

    @Test
    public void first_second_state() {
        throw new RuntimeException();
    }

    @Test
    public void first_second_third_state() {
        throw new RuntimeException();
    }

    @NonNull
    private static HistoryState saveRestore(@NonNull HistoryState state) {

        final Parcel in = Parcel.obtain();
        final byte[] bytes;
        try {
            state.writeToParcel(in, 0);
            bytes = in.marshall();
        } finally {
            in.recycle();
        }

        final Parcel out = Parcel.obtain();
        try {
            out.unmarshall(bytes, 0, bytes.length);
            return HistoryState.CREATOR.createFromParcel(out);
        } finally {
            out.recycle();
        }
    }

    private static int size(@NonNull HistoryState state) {
        //noinspection unchecked
        return state.entries().size();
    }

    @NonNull
    private static Key keyAt(@NonNull HistoryState state, int index) {
        //noinspection unchecked
        final Entry entry = state.entries().get(index);
        return (Key) entry.key();
    }

    @NonNull
    private static <P extends Parcelable> P stateAt(@NonNull HistoryState state, int index) {
        //noinspection unchecked
        final Entry entry = state.entries().get(index);
        //noinspection unchecked
        return (P) entry.state();
    }

    public static class FirstState implements Parcelable {

        private final int i;

        FirstState(int i) {
            this.i = i;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.i);
        }

        protected FirstState(Parcel in) {
            this.i = in.readInt();
        }

        public static final Parcelable.Creator<FirstState> CREATOR = new Parcelable.Creator<FirstState>() {
            @Override
            public FirstState createFromParcel(Parcel source) {
                return new FirstState(source);
            }

            @Override
            public FirstState[] newArray(int size) {
                return new FirstState[size];
            }
        };
    }

    public static class SecondState implements Parcelable {

        private final boolean b;

        SecondState(boolean b) {
            this.b = b;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.b ? (byte) 1 : (byte) 0);
        }

        protected SecondState(Parcel in) {
            this.b = in.readByte() != 0;
        }

        public static final Parcelable.Creator<SecondState> CREATOR = new Parcelable.Creator<SecondState>() {
            @Override
            public SecondState createFromParcel(Parcel source) {
                return new SecondState(source);
            }

            @Override
            public SecondState[] newArray(int size) {
                return new SecondState[size];
            }
        };
    }

    public static class ThirdState implements Parcelable {

        private final String s;

        ThirdState(@NonNull String s) {
            this.s = s;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.s);
        }

        protected ThirdState(Parcel in) {
            this.s = in.readString();
        }

        public static final Parcelable.Creator<ThirdState> CREATOR = new Parcelable.Creator<ThirdState>() {
            @Override
            public ThirdState createFromParcel(Parcel source) {
                return new ThirdState(source);
            }

            @Override
            public ThirdState[] newArray(int size) {
                return new ThirdState[size];
            }
        };
    }
}
