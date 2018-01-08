package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Adapter implementation of {@link ScreenLifecycleCallbacks}
 */
@SuppressWarnings("unused")
public abstract class ScreenLifecycleCallbacksAdapter<K extends Enum<K>> implements ScreenLifecycleCallbacks<K> {
    @Override
    public void init(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager) {

    }

    @Override
    public void destroy(@NonNull Screen<K, ? extends Parcelable> screen) {

    }

    @Override
    public void onAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {

    }

    @Override
    public void onDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {

    }

    @Override
    public void onActive(@NonNull Screen<K, ? extends Parcelable> screen) {

    }

    @Override
    public void onInactive(@NonNull Screen<K, ? extends Parcelable> screen) {

    }
}
