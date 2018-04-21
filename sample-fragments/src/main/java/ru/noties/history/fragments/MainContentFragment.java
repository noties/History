package ru.noties.history.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import ru.noties.history.Entry;
import ru.noties.history.History;

public class MainContentFragment extends HistoryFragment<ScreenKey> {

    private static final String KEY_STATE = "key.State";

    public static MainContentFragment newInstance(@NonNull MainContentState state) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STATE, state);

        final MainContentFragment fragment = new MainContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_content, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MainContentState state = state();

        final TextView text = view.findViewById(R.id.text);

        text.setText(String.format(Locale.US, "[%d]", state.index()));
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history().push(Entry.create(ScreenKey.MAIN_CONTENT, new MainContentState(state.index() + 1)));
            }
        });

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final History<ScreenKey> history = history();
                if (history.length() > 1) {
                    history.drop(history.entryAt(history.length() - 2));
                    return true;
                }
                return false;
            }
        });
    }

    @NonNull
    private MainContentState state() {
        //noinspection ConstantConditions
        return getArguments().getParcelable(KEY_STATE);
    }
}
