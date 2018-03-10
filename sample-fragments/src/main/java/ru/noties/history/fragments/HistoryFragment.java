package ru.noties.history.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import ru.noties.history.History;

public abstract class HistoryFragment<K extends Enum<K>> extends Fragment {

    private HistoryProvider<K> provider;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof HistoryProvider)) {
            throw new IllegalStateException("Holding context must implement: " + HistoryProvider.class.getName());
        }

        //noinspection unchecked
        provider = (HistoryProvider<K>) context;
    }

    @NonNull
    public History<K> history() {
        return provider.provideHistory();
    }
}
