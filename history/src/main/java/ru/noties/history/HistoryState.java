package ru.noties.history;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class HistoryState implements Parcelable {

    @Nullable
    public static HistoryState restore(@Nullable Parcelable parcelable) {
        return parcelable instanceof HistoryState
                ? (HistoryState) parcelable
                : null;
    }

    @Nullable
    public static HistoryState restore(@Nullable Bundle bundle, @NonNull String key) {
        return bundle != null
                ? restore(bundle.getParcelable(key))
                : null;
    }

    private final List<Entry> entries;

    HistoryState(@NonNull List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    <K extends Enum<K>> List<Entry<K>> entries() {
        //noinspection unchecked
        return (List<Entry<K>>) (List) entries;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // first write size of our entries
        // then obtain entry enum type and write it
        // then iterate over all items and write:
        //      key ordinal
        //      parcelable type
        //      parcelable value

        //noinspection unchecked
        final List<Entry<? extends Enum>> list = (List<Entry<? extends Enum>>) (List) entries;

        final int length = list.size();
        if (length == 0) {

            dest.writeInt(0);

        } else {

            // total length
            dest.writeInt(length);

            Entry<? extends Enum> entry;

            for (int i = 0; i < length; i++) {
                entry = list.get(i);
                if (i == 0) {
                    // enum type (just use the first one)
                    writeType(dest, entry.key());
                }
                dest.writeInt(entry.key().ordinal());
                writeType(dest, entry.state());
                dest.writeParcelable(entry.state(), flags);
            }
        }
    }

    private static void writeType(@NonNull Parcel parcel, @NonNull Object o) {
        parcel.writeString(o.getClass().getName());
    }

    private HistoryState(Parcel in) {

        final int length = in.readInt();

        if (length == 0) {

            entries = Collections.emptyList();

        } else {

            entries = new ArrayList<>(length);

            final Enum[] constants = readEnumConstants(in);

            int ordinal;
            Parcelable parcelable;

            for (int i = 0; i < length; i++) {
                ordinal = in.readInt();
                parcelable = readParcelable(in);
                //noinspection unchecked
                entries.add(new Entry(constants[ordinal], parcelable));
            }
        }
    }

    @NonNull
    private static Enum[] readEnumConstants(@NonNull Parcel parcel) {
        try {
            //noinspection unchecked
            return ((Class<Enum>) Class.forName(parcel.readString())).getEnumConstants();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to read Enum type from supplied Parcel");
        }
    }

    @NonNull
    private static Parcelable readParcelable(@NonNull Parcel parcel) {
        try {
            final Class<?> cl = Class.forName(parcel.readString());
            return parcel.readParcelable(cl.getClassLoader());
        } catch (Throwable t) {
            throw new RuntimeException("Unable to read Parcelable for supplied Parcel");
        }
    }

    public static final Parcelable.Creator<HistoryState> CREATOR = new Parcelable.Creator<HistoryState>() {
        @Override
        public HistoryState createFromParcel(Parcel source) {
            return new HistoryState(source);
        }

        @Override
        public HistoryState[] newArray(int size) {
            return new HistoryState[size];
        }
    };
}
