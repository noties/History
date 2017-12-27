package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import ru.noties.history.Entry;
import ru.noties.history.screen.Screen;

public class StartScreen extends Screen<ScreenKey, StartState> {

    public StartScreen(@NonNull ScreenKey key, @NonNull StartState state) {
        super(key, state);
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.screen_start, parent, false);
    }

    @Override
    public void onAttach(@NonNull View view) {
        super.onAttach(view);

        final TextView text = view.findViewById(R.id.text);
        text.setText(String.format(Locale.getDefault(), "[%d]", state.index()));

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.index() == 5) {
                    history().push(Entry.create(ScreenKey.DIALOG));
                } else {
                    history().push(Entry.create(ScreenKey.START, new StartState(state.index() + 1)));
                }
            }
        });
    }
}
