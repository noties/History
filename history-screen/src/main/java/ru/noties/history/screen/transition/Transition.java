package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.view.View;

// must be stateless
public interface Transition {

    interface Callback {
        void cancel();
    }

    @NonNull
    Callback animate(@NonNull View from, @NonNull View to, @NonNull Runnable endAction);
}
