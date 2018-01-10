package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class SingleViewChange implements SingleChange {

    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view
    );

    protected abstract void startAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view,
            @NonNull Runnable endAction
    );

    protected abstract void cancelAnimation(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View view
    );


    @NonNull
    @Override
    public final ChangeCallback apply(
            boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen screen,
            @NonNull Runnable endAction
    ) {
        return apply(reverse, manager.container(), screen.view(), endAction);
    }

    @NonNull
    protected ChangeCallback apply(
            final boolean reverse,
            @NonNull final ViewGroup container,
            @NonNull final View view,
            @NonNull final Runnable endAction
    ) {

        applyStartValues(reverse, container, view);

        startAnimation(reverse, container, view, endAction);

        return new ChangeCallback() {
            @Override
            public void cancel() {
                cancelAnimation(reverse, container, view);
                endAction.run();
            }
        };
    }
}
