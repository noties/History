package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.history.Entry;

@Deprecated
class TransitionControllerImpl<K extends Enum<K>> extends TransitionController<K> {

    private final Transition forward;
    private final Transition back;

    TransitionControllerImpl(@NonNull Transition forward, @NonNull Transition back) {
        this.forward = forward;
        this.back = back;
    }

    @NonNull
    @Override
    public Transition.Callback forward(@Nullable Entry<K> from, @NonNull Entry<K> to, @Nullable View fromView, @NonNull View toView, @NonNull Runnable endAction) {
        return fromView == null
                ? noOp(endAction)
                : forward.animate(fromView, toView, endAction);
    }

    @NonNull
    @Override
    public Transition.Callback back(@NonNull Entry<K> from, @Nullable Entry<K> to, @NonNull View fromView, @Nullable View toView, @NonNull Runnable endAction) {
        return toView == null
                ? noOp(endAction)
                : back.animate(fromView, toView, endAction);
    }

    @NonNull
    private Transition.Callback noOp(@NonNull Runnable endAction) {
        endAction.run();
        return NO_OP;
    }

    private static final Transition.Callback NO_OP = new Transition.Callback() {
        @Override
        public void cancel() {
            // no op
        }
    };
}
