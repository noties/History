package ru.noties.history.sample;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.noties.screen.Screen;
import ru.noties.screen.transit.Edge;
import ru.noties.screen.transit.SlideSwitch;
import ru.noties.screen.transit.SwitchController;
import ru.noties.screen.transit.SwitchEngine;
import ru.noties.screen.transit.SwitchEngineCallback;
import ru.noties.screen.transit.ValueAnimatorEngine;
import ru.noties.screen.transit.tweens.AccordionTweenEngine;
import ru.noties.screen.transit.tweens.CubeOutTweenEngine;
import ru.noties.screen.transit.tweens.DepthTween;
import ru.noties.screen.transit.tweens.ParallaxTween;
import ru.noties.screen.transit.tweens.TweenSwitchEngine;
import ru.noties.screen.transit.tweens.ZoomOutTween;

public class AllSwitchController extends SwitchController<ScreenKey> {

    private final List<SwitchEngine<ScreenKey>> engines;

    public AllSwitchController(@NonNull Edge fromEdge) {
        this.engines = create(fromEdge);
    }

    @Nullable
    @Override
    protected SwitchEngineCallback apply(boolean reverse, @NonNull Screen<ScreenKey, ? extends Parcelable> from, @NonNull Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {
        return switchEngine(from, to).apply(reverse, from, to, endAction);
    }

    @NonNull
    @Override
    public SwitchEngine<ScreenKey> switchEngine(@NonNull Screen<ScreenKey, ? extends Parcelable> from, @NonNull Screen<ScreenKey, ? extends Parcelable> to) {
        final ContentState state = (ContentState) from.state();
        return engines.get(state.value() % engines.size());
    }

    @NonNull
    private List<SwitchEngine<ScreenKey>> create(@NonNull Edge edge) {
        final long duration = 250L;
        final List<SwitchEngine<ScreenKey>> list = new ArrayList<>();
        list.add(ValueAnimatorEngine.<ScreenKey>create(SlideSwitch.from(edge), duration));
        list.add(AccordionTweenEngine.create(edge, duration));
        list.add(CubeOutTweenEngine.create(edge, duration));
        list.add(TweenSwitchEngine.create(ScreenKey.class, DepthTween.create(edge, duration)));
        list.add(TweenSwitchEngine.create(ScreenKey.class, ParallaxTween.create(edge, duration)));
        list.add(TweenSwitchEngine.create(ScreenKey.class, ZoomOutTween.create(edge, duration * 2)));
        return list;
    }
}
