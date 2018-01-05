package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public interface Change {

    @NonNull
    ChangeCallback animate(
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    );
}
