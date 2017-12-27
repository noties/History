package ru.noties.history.sample;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.debug.Debug;
import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenLifecycleCallbacks;
import ru.noties.history.screen.ScreenManager;

class LoggingScreenLifecycleCallbacks<K extends Enum<K>> implements ScreenLifecycleCallbacks<K> {

    @Override
    public void init(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager) {
        Debug.i("screen: %s, manager: %s", screen, manager);
    }

    @Override
    public void destroy(@NonNull Screen<K, ? extends Parcelable> screen) {
        Debug.i("screen: %s", screen);
    }

    @Override
    public void onAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {
        Debug.i("screen: %s, view: %s", screen, view);
    }

    @Override
    public void onDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {
        Debug.i("screen: %s, view: %s", screen, view);
    }

    @Override
    public void onActive(@NonNull Screen<K, ? extends Parcelable> screen) {
        Debug.i("screen: %s", screen);
    }

    @Override
    public void onInactive(@NonNull Screen<K, ? extends Parcelable> screen) {
        Debug.i("screen: %s", screen);
    }
}
