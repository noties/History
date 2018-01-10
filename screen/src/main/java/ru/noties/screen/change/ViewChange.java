package ru.noties.screen.change;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

@SuppressWarnings("WeakerAccess")
public abstract class ViewChange extends Change {

    @SuppressWarnings("unused")
    @NonNull
    public <K extends Enum<K>> Change<K> cast(@NonNull Class<K> type) {
        //noinspection unchecked
        return (Change<K>) this;
    }

    protected abstract void applyStartValues(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to
    );

    protected abstract void executeChange(
            boolean reverse,
            @NonNull ViewGroup container,
            @NonNull View from,
            @NonNull View to,
            @NonNull Runnable endAction
    );

    protected abstract void cancelChange(
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

    // final so no erased type info is accessed
    @NonNull
    @Override
    protected final ChangeCallback applyNow(
            final boolean reverse,
            @NonNull ScreenManager manager,
            @NonNull Screen from,
            @NonNull Screen to,
            @NonNull final Runnable endAction
    ) {

        //noinspection unchecked
        return super.applyNow(reverse, manager, from, to, endAction);

//        final ViewGroup container = manager.container();
//        final View fromView = from.view();
//        final View toView = to.view();
//
//        applyStartValues(reverse, container, fromView, toView);
//
//        executeChange(reverse, container, fromView, toView, endAction);
//
//        return new ChangeCallback() {
//            @Override
//            public void cancel() {
//                cancelChange(reverse, container, fromView, toView);
//                endAction.run();
//            }
//        };
    }

    @Override
    protected final void applyStartValues(boolean reverse, @NonNull ScreenManager manager, @NonNull Screen from, @NonNull Screen to) {
        applyStartValues(reverse, manager.container(), from.view(), to.view());
    }

    @Override
    protected final void executeChange(boolean reverse, @NonNull ScreenManager manager, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        executeChange(reverse, manager.container(), from.view(), to.view(), endAction);
    }

    @Override
    protected final void cancelChange(boolean reverse, @NonNull ScreenManager manager, @NonNull Screen from, @NonNull Screen to) {
        cancelChange(reverse, manager.container(), from.view(), to.view());
    }
}
