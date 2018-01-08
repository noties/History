package ru.noties.history;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * An empty state to be used when creating {@link Entry} that requires no state.
 * This class will be used by default when {@link Entry#create(Enum)} is called
 * without explicit state provided
 *
 * @see #instance()
 */
@SuppressWarnings("WeakerAccess")
public final class EmptyState implements Parcelable {

    private static final EmptyState INSTANCE = new EmptyState();

    @NonNull
    public static EmptyState instance() {
        return INSTANCE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) 0);
    }

    private EmptyState() {
    }

    public static final Creator<EmptyState> CREATOR = new Creator<EmptyState>() {
        @Override
        public EmptyState createFromParcel(Parcel source) {
            source.readByte();
            return INSTANCE;
        }

        @Override
        public EmptyState[] newArray(int size) {
            return new EmptyState[size];
        }
    };
}
