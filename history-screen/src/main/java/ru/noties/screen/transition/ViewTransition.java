package ru.noties.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.noties.screen.Screen;

/**
 * A subclass of {@link ScreenTransition} to allow creation of type indifferent transitions which
 * operate on android.view.View and not on {@link Screen}. All methods that receive type information
 * from a {@link Screen} are <em>final</em>, so a subclass of this class won\'t be able to access it
 *
 * @see ScreenTransition
 */
@SuppressWarnings("unused")
public abstract class ViewTransition extends ScreenTransition {


    @SuppressWarnings("WeakerAccess")
    @Nullable
    protected abstract TransitionCallback applyNow(boolean reverse, @NonNull View from, @NonNull View to, @NonNull Runnable endAction);


    @NonNull
    public final <K extends Enum<K>> ScreenTransition<K> cast() {
        //noinspection unchecked
        return this;
    }

    @NonNull
    public final <K extends Enum<K>> ScreenTransition<K> cast(@NonNull Class<K> type) {
        //noinspection unchecked
        return this;
    }

    @Nullable
    @Override
    public final TransitionCallback apply(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        //noinspection unchecked
        return super.apply(reverse, from, to, endAction);
    }

    @Nullable
    @Override
    protected final TransitionCallback applyWhenReady(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        //noinspection unchecked
        return super.applyWhenReady(reverse, from, to, endAction);
    }

    @Nullable
    @Override
    protected final TransitionCallback applyNow(boolean reverse, @NonNull Screen from, @NonNull Screen to, @NonNull Runnable endAction) {
        return applyNow(reverse, from.view(), to.view(), endAction);
    }
}
