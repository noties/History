package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.view.View;

@Deprecated
public class TransitionNoOp implements Transition {

    @NonNull
    public static TransitionNoOp instance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public Callback animate(@NonNull View from, @NonNull View to, @NonNull Runnable endAction) {
        endAction.run();
        return CALLBACK;
    }

    private static final TransitionNoOp INSTANCE = new TransitionNoOp();

    private static final Callback CALLBACK = new Callback() {
        @Override
        public void cancel() {
            // no op
        }
    };
}
