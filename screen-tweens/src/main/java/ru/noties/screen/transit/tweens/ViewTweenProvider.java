package ru.noties.screen.transit.tweens;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.tumbleweed.BaseTweenDef;

@SuppressWarnings({"WeakerAccess", "unused"})
public interface ViewTweenProvider {

    @NonNull
    BaseTweenDef provide(
            boolean reverse,
            @NonNull View from,
            @NonNull View to
    );
}
