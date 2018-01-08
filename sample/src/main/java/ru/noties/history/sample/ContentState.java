package ru.noties.history.sample;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

public class ContentState implements Parcelable {

    private final int value;
    private final int color;

    public ContentState(int value, @ColorInt int color) {
        this.value = value;
        this.color = color;
    }

    public int value() {
        return value;
    }

    @ColorInt
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return "ContentState{" +
                "value=" + value +
                ", color=" + color +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
        dest.writeInt(this.color);
    }

    private ContentState(Parcel in) {
        this.value = in.readInt();
        this.color = in.readInt();
    }

    public static final Parcelable.Creator<ContentState> CREATOR = new Parcelable.Creator<ContentState>() {
        @Override
        public ContentState createFromParcel(Parcel source) {
            return new ContentState(source);
        }

        @Override
        public ContentState[] newArray(int size) {
            return new ContentState[size];
        }
    };
}
