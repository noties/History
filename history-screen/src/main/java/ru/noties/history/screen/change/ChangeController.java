package ru.noties.history.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenManager;

public abstract class ChangeController<K extends Enum<K>> {

    // so, we must have at least 2 things: provide independent 2 animations (not linked)
    // an animation that deal with both views

    // also, as we are having type info included, we might want to create some abstraction
    // to allow usage of type-less changeHandlers (alpha, slide, whatever)

    @NonNull
    public abstract ChangeCallback forward(
            @NonNull ScreenManager<K> manager,
            @Nullable Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @NonNull
    public abstract ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull Screen<K, ? extends Parcelable> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    @SuppressWarnings("unused")
    @NonNull
    public ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull View view,
            @NonNull Runnable endAction
    ) {
        return back(manager, from.get(from.size() - 1), to, endAction);
    }
}
