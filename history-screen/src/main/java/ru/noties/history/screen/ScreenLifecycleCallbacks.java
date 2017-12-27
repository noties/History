package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

public interface ScreenLifecycleCallbacks<K extends Enum<K>> {


    void init(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager);

    void destroy(@NonNull Screen<K, ? extends Parcelable> screen);


    void onAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view);

    void onDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view);


    void onActive(@NonNull Screen<K, ? extends Parcelable> screen);

    void onInactive(@NonNull Screen<K, ? extends Parcelable> screen);
}
