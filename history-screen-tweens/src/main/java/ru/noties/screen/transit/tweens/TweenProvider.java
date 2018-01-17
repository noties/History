package ru.noties.screen.transit.tweens;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.screen.Screen;
import ru.noties.tumbleweed.BaseTweenDef;

@SuppressWarnings("unused")
public interface TweenProvider<K extends Enum<K>> {

    @NonNull
    BaseTweenDef provide(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );
}
