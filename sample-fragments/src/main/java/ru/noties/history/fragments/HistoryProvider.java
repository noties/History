package ru.noties.history.fragments;

import android.support.annotation.NonNull;

import ru.noties.history.History;

public interface HistoryProvider<K extends Enum<K>> {

    @NonNull
    History<K> provideHistory();
}
