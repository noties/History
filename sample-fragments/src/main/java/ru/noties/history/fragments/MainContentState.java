package ru.noties.history.fragments;

import android.os.Parcel;
import android.os.Parcelable;

public class MainContentState implements Parcelable {

    private final int index;

    public MainContentState(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
    }

    protected MainContentState(Parcel in) {
        this.index = in.readInt();
    }

    public static final Parcelable.Creator<MainContentState> CREATOR = new Parcelable.Creator<MainContentState>() {
        @Override
        public MainContentState createFromParcel(Parcel source) {
            return new MainContentState(source);
        }

        @Override
        public MainContentState[] newArray(int size) {
            return new MainContentState[size];
        }
    };
}
