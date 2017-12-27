package ru.noties.history.sample;

import android.os.Parcel;
import android.os.Parcelable;

public class StartState implements Parcelable {

    private final int index;

    private long firstShown;

    public StartState(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    public StartState firstShown(long firstShown) {
        this.firstShown = firstShown;
        return this;
    }

    public long firstShown() {
        return firstShown;
    }

    @Override
    public String toString() {
        return "StartState{" +
                "index=" + index +
                ", firstShown=" + firstShown +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeLong(this.firstShown);
    }

    private StartState(Parcel in) {
        this.index = in.readInt();
        this.firstShown = in.readLong();
    }

    public static final Parcelable.Creator<StartState> CREATOR = new Parcelable.Creator<StartState>() {
        @Override
        public StartState createFromParcel(Parcel source) {
            return new StartState(source);
        }

        @Override
        public StartState[] newArray(int size) {
            return new StartState[size];
        }
    };
}
