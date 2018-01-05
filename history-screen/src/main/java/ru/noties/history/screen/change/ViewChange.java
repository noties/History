package ru.noties.history.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class ViewChange extends Change {


    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to
    );

    protected abstract void startAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    );

    protected abstract void cancelAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to
    );

    // final so no erased type info is not accessed
    @NonNull
    @Override
    public final ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen from,
            @NonNull Screen to,
            @NonNull Runnable endAction
    ) {
        //noinspection unchecked
        return super.apply(reverse, manager, from, to, endAction);
    }

    // final so no erased type info is not accessed
    @NonNull
    @Override
    protected final ChangeCallback applyNow(
            final boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen from,
            @NonNull Screen to,
            @NonNull final Runnable endAction
    ) {

        final ViewGroup container = manager.container();
        final View fromView = from.view();
        final View toView = to.view();

        applyStartValues(reverse, container, fromView, toView);

        startAnimation(reverse, container, fromView, toView, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelAnimation(reverse, container, fromView, toView);
                endAction.run();
            }
        };
    }
}
