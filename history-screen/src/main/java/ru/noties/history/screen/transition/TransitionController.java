package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.history.Entry;

@Deprecated
public abstract class TransitionController<K extends Enum<K>> {

    @NonNull
    public static <K extends Enum<K>> TransitionController<K> create(@NonNull Transition forward, @NonNull Transition back) {
        return new TransitionControllerImpl<>(forward, back);
    }

    @SuppressWarnings("unused")
    @NonNull
    public static <K extends Enum<K>> TransitionControllerBuilder<K> builder(@NonNull Class<K> type) {
        return new TransitionControllerBuilder<>();
    }

    @NonNull
    public abstract Transition.Callback forward(
            @Nullable Entry<K> from,
            @NonNull Entry<K> to,
            @Nullable View fromView,
            @NonNull View toView,
            @NonNull Runnable endAction
    );

    @NonNull
    public abstract Transition.Callback back(
            @NonNull Entry<K> from,
            @Nullable Entry<K> to,
            @NonNull View fromView,
            @Nullable View toView,
            @NonNull Runnable endAction
    );
}
