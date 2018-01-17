package ru.noties.screen.transit;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public class CompositeScreenSwitch<K extends Enum<K>> extends ScreenSwitch<K> {

    @SuppressWarnings("unchecked")
    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> create(@NonNull ScreenSwitch<K> first, @NonNull ScreenSwitch<K> second) {
        final List<ScreenSwitch<K>> list = new ArrayList<>(3);
        list.add(first);
        list.add(second);
        return new CompositeScreenSwitch<>(list);
    }

    @NonNull
    public static <K extends Enum<K>> ScreenSwitch<K> create(@NonNull Collection<ScreenSwitch<K>> collection) {
        return new CompositeScreenSwitch<>(new ArrayList<>(collection));
    }

    private final List<ScreenSwitch<K>> list;

    private CompositeScreenSwitch(@NonNull List<ScreenSwitch<K>> list) {
        this.list = list;
    }

    @Override
    public void apply(float fraction, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        for (ScreenSwitch<K> screenSwitch : list) {
            screenSwitch.apply(fraction, from, to);
        }
    }
}
