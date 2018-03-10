package ru.noties.history.sample;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.noties.screen.Screen;
import ru.noties.screen.transition.AccordionTransition;
import ru.noties.screen.transition.CubeOutTransition;
import ru.noties.screen.transition.DepthTransition;
import ru.noties.screen.transition.Edge;
import ru.noties.screen.transition.ParallaxTransition;
import ru.noties.screen.transition.ScreenTransition;
import ru.noties.screen.transition.SlideTransition;
import ru.noties.screen.transition.TransitionCallback;
import ru.noties.screen.transition.TransitionController;
import ru.noties.screen.transition.ZoomOutTransition;

public class AllTransitionsController extends TransitionController<ScreenKey> {

    @NonNull
    public static AllTransitionsController create() {

        final long duration = 250L;

        final List<ScreenTransition<ScreenKey>> list = new ArrayList<>();

        // Slide (bundled)
        {
            list.add(SlideTransition.create(Edge.RIGHT, duration));
            list.add(SlideTransition.create(Edge.BOTTOM, duration));
            list.add(SlideTransition.create(Edge.LEFT, duration));
            list.add(SlideTransition.create(Edge.TOP, duration));
        }

        // Accordion (transitions)
        {
            list.add(AccordionTransition.create(Edge.RIGHT, duration));
            list.add(AccordionTransition.create(Edge.BOTTOM, duration));
            list.add(AccordionTransition.create(Edge.LEFT, duration));
            list.add(AccordionTransition.create(Edge.TOP, duration));
        }

        // Cube Out (transitions)
        {
            list.add(CubeOutTransition.create(Edge.RIGHT, duration));
            list.add(CubeOutTransition.create(Edge.BOTTOM, duration));
            list.add(CubeOutTransition.create(Edge.LEFT, duration));
            list.add(CubeOutTransition.create(Edge.TOP, duration));
        }

        // Depth (transitions)
        {
            list.add(DepthTransition.create(Edge.RIGHT, duration));
            list.add(DepthTransition.create(Edge.BOTTOM, duration));
            list.add(DepthTransition.create(Edge.LEFT, duration));
            list.add(DepthTransition.create(Edge.TOP, duration));
        }

        // Parallax (transitions)
        {
            list.add(ParallaxTransition.create(Edge.RIGHT, duration));
            list.add(ParallaxTransition.create(Edge.BOTTOM, duration));
            list.add(ParallaxTransition.create(Edge.LEFT, duration));
            list.add(ParallaxTransition.create(Edge.TOP, duration));
        }

        // Zoom Out (transitions)
        {
            list.add(ZoomOutTransition.create(Edge.RIGHT, duration));
            list.add(ZoomOutTransition.create(Edge.BOTTOM, duration));
            list.add(ZoomOutTransition.create(Edge.LEFT, duration));
            list.add(ZoomOutTransition.create(Edge.TOP, duration));
        }

        return new AllTransitionsController(list);
    }

    private final List<ScreenTransition<ScreenKey>> transitions;

    private AllTransitionsController(@NonNull List<ScreenTransition<ScreenKey>> transitions) {
        this.transitions = transitions;
    }

    @Nullable
    @Override
    public TransitionCallback forward(@Nullable Screen<ScreenKey, ? extends Parcelable> from, @NonNull Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {
        return from == null
                ? TransitionCallback.noOp(endAction)
                : transition(from).apply(false, from, to, endAction);
    }

    @Nullable
    @Override
    public TransitionCallback back(@NonNull Screen<ScreenKey, ? extends Parcelable> from, @Nullable Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {
        return to == null
                ? TransitionCallback.noOp(endAction)
                : transition(to).apply(true, to, from, endAction);
    }

    @NonNull
    private ScreenTransition<ScreenKey> transition(@NonNull Screen<ScreenKey, ? extends Parcelable> screen) {
        final ContentState state = (ContentState) screen.state();
        return transitions.get(state.value() % transitions.size());
    }
}
