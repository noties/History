package ru.noties.screen.transit.tweens;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.noties.screen.Screen;
import ru.noties.tumbleweed.BaseTweenDef;

class TweenSwitchEngineImpl<K extends Enum<K>> extends TweenSwitchEngine<K> {

    private final TweenProvider<K> provider;

    TweenSwitchEngineImpl(@NonNull TweenProvider<K> provider) {
        this.provider = provider;
    }

    @NonNull
    @Override
    protected BaseTweenDef createTween(boolean reverse, @NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
        return provider.provide(reverse, from, to);
    }
}
