package ru.noties.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * An interface to be notified when underlying {@link ScreenManager} state is changed.
 *
 * These methods will be called <strong>before</strong> being dispatched to actual {@link Screen}.
 * This will allow slight modification of {@link Screen} state before it will receive actual event.
 * Can be useful for example for injecting dependencies.
 *
 * @see ScreenManager#screenCallbacks(ScreenLifecycleCallbacks)
 */
public interface ScreenLifecycleCallbacks<K extends Enum<K>> {


    void init(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager);

    void destroy(@NonNull Screen<K, ? extends Parcelable> screen);


    void onAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view);

    void onDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view);


    void onActive(@NonNull Screen<K, ? extends Parcelable> screen);

    void onInactive(@NonNull Screen<K, ? extends Parcelable> screen);
}
