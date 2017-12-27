package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.EmptyState;
import ru.noties.history.screen.Screen;

public class DialogScreen extends Screen<ScreenKey, EmptyState> {

    public DialogScreen(@NonNull ScreenKey key, @NonNull EmptyState state) {
        super(key, state);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.screen_dialog, parent, false);
    }
}
