package ru.noties.history.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;

public abstract class HistoryManager<K extends Enum<K>> {

    public interface Provider<K extends Enum<K>> {

        @NonNull
        Fragment fragment(@NonNull Entry<K> entry);
    }

    @NonNull
    public static <K extends Enum<K>> HistoryManager<K> create(
            @NonNull History<K> history,
            @NonNull Provider<K> provider,
            @NonNull FragmentManager fragmentManager,
            @IdRes int containerId
    ) {
        return new HistoryManagerImpl<>(history, provider, fragmentManager, containerId);
    }

    public abstract boolean goBack();

    @NonNull
    public abstract History<K> history();

    @NonNull
    public abstract HistoryState save();

    public abstract boolean restore(@Nullable HistoryState state);

    public abstract boolean restore(@Nullable Bundle savedInstanceState, @NonNull String key);
}
