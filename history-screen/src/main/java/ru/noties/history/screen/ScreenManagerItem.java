package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.history.Entry;

class ScreenManagerItem<K extends Enum<K>> {

    final Entry<K> entry;
    final Screen<K, ? extends Parcelable> screen;
    View view;

    ScreenManagerItem(@NonNull Entry<K> entry, @NonNull Screen<K, ? extends Parcelable> screen) {
        this.entry = entry;
        this.screen = screen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenManagerItem screenManagerItem = (ScreenManagerItem) o;

        return entry.equals(screenManagerItem.entry);
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }

    @Override
    public String toString() {
        return "ScreenManagerItem{" +
                "entry=" + entry +
                ", screen=" + screen +
                ", view=" + view +
                '}';
    }
}
