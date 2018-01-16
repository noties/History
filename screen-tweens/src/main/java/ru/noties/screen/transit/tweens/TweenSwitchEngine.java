package ru.noties.screen.transit.tweens;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewTreeObserver;

import ru.noties.screen.Screen;
import ru.noties.screen.transit.SwitchEngine;
import ru.noties.screen.transit.SwitchEngineCallback;
import ru.noties.tumbleweed.BaseTween;
import ru.noties.tumbleweed.BaseTweenDef;
import ru.noties.tumbleweed.TweenCallback;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.ViewTweenManager;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class TweenSwitchEngine<K extends Enum<K>> extends SwitchEngine<K> {

    // small delta to make a tween apply start values
    private static final float UPDATE = 0.0001F;

    @NonNull
    public static <K extends Enum<K>> TweenSwitchEngine<K> create(@NonNull TweenProvider<K> provider) {
        return new TweenSwitchEngineImpl<>(provider);
    }

    @NonNull
    public static <K extends Enum<K>> TweenSwitchEngine<K> create(@NonNull Class<K> type, @NonNull ViewTweenProvider provider) {
        return new TweenSwitchEngineImpl<>(new ProviderDelegate<K>(provider));
    }

    protected TweenSwitchEngine() {
        //noinspection ConstantConditions
        super(null);
    }

    @Nullable
    @Override
    protected SwitchEngineCallback applyNow(
            final boolean reverse,
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to,
            @NonNull final Runnable endAction
    ) {

        before(reverse, from, to);

        final BaseTween tween = createTween(reverse, from, to)
                .addCallback(TweenCallback.END, new TweenCallback() {
                    @Override
                    public void onEvent(int type, @NonNull BaseTween source) {
                        after(reverse, from, to);
                        endAction.run();
                    }
                })
                .start(tweenManager(from));

        // if we are going reverse, values must be applied already
        if (!reverse) {
            // un-f-fortunately we cannot rely on `set` calls (need to be updated)
            // tween must be initialized... so we must force update now
            updateTween(tween);
        }

        return new SwitchEngineCallback() {
            @Override
            public void cancel() {
                after(reverse, from, to);
                tween.kill();
                endAction.run();
            }
        };
    }

    @Override
    public void forceEndValues(
            @NonNull final Screen<K, ? extends Parcelable> from,
            @NonNull final Screen<K, ? extends Parcelable> to
    ) {

        if (isReady(from)
                && isReady(to)) {

            before(true, from, to);
            updateTween(createTween(true, from, to).start());
            after(true, from, to);

        } else {

            final ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    if (isReady(from)
                            && isReady(to)) {

                        removeOnPreDrawListener(from, this);
                        removeOnPreDrawListener(to, this);

                        before(true, from, to);
                        updateTween(createTween(true, from, to).start());
                        after(true, from, to);

                        return true;
                    }

                    return false;
                }
            };

            addOnPreDrawListener(from, listener);
            addOnPreDrawListener(to, listener);
        }
    }

    @NonNull
    protected abstract BaseTweenDef createTween(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    );

    protected void before(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    ) {
        // no op
    }

    protected void after(
            boolean reverse,
            @NonNull Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to
    ) {
        // no op
    }

    @NonNull
    protected static TweenManager tweenManager(@NonNull Screen<?, ?> screen) {
        return ViewTweenManager.get(R.id.tween_switch_engine, screen.manager().container());
    }

    protected static void updateTween(@NonNull BaseTween tween) {
        tween.update(UPDATE);
    }


    private static class ProviderDelegate<K extends Enum<K>> implements TweenProvider<K> {

        private final ViewTweenProvider provider;

        ProviderDelegate(@NonNull ViewTweenProvider provider) {
            this.provider = provider;
        }

        @NonNull
        @Override
        public BaseTweenDef provide(
                boolean reverse,
                @NonNull Screen<K, ? extends Parcelable> from,
                @NonNull Screen<K, ? extends Parcelable> to
        ) {
            return provider.provide(reverse, from.view(), to.view());
        }
    }
}
