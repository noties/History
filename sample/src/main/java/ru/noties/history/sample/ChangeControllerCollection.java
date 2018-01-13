package ru.noties.history.sample;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;
import ru.noties.screen.change.AlphaViewChange;
import ru.noties.screen.change.Change;
import ru.noties.screen.change.ChangeCallback;
import ru.noties.screen.change.ChangeCallbackNoOp;
import ru.noties.screen.change.ChangeController;
import ru.noties.screen.change.SlideViewChange;
import ru.noties.screen.changes.AccordionViewChange;
import ru.noties.screen.changes.CubeOutViewChange;
import ru.noties.screen.changes.DepthViewChange;
import ru.noties.screen.changes.ParallaxViewChange;
import ru.noties.screen.changes.ZoomOutViewChange;

class ChangeControllerCollection extends ChangeController<ScreenKey> {

    @SuppressWarnings("SameParameterValue")
    @NonNull
    static ChangeControllerCollection create(long duration) {

        final List<Change<ScreenKey>> changes = new ArrayList<>();

        changes.add(CubeOutViewChange.fromLeft(duration));
        changes.add(CubeOutViewChange.fromTop(duration));
        changes.add(CubeOutViewChange.fromRight(duration));
        changes.add(CubeOutViewChange.fromBottom(duration));

        changes.add(DepthViewChange.fromLeft(duration));
        changes.add(DepthViewChange.fromTop(duration));
        changes.add(DepthViewChange.fromRight(duration));
        changes.add(DepthViewChange.fromBottom(duration));

        changes.add(ZoomOutViewChange.fromLeft(duration * 2));
        changes.add(ZoomOutViewChange.fromTop(duration * 2));
        changes.add(ZoomOutViewChange.fromRight(duration * 2));
        changes.add(ZoomOutViewChange.fromBottom(duration * 2));

        changes.add(AccordionViewChange.fromLeft(duration));
        changes.add(AccordionViewChange.fromTop(duration));
        changes.add(AccordionViewChange.fromRight(duration));
        changes.add(AccordionViewChange.fromBottom(duration));

        changes.add(ParallaxViewChange.fromLeft(duration));
        changes.add(ParallaxViewChange.fromTop(duration));
        changes.add(ParallaxViewChange.fromRight(duration));
        changes.add(ParallaxViewChange.fromBottom(duration));

        changes.add(SlideViewChange.fromLeft(duration));
        changes.add(SlideViewChange.fromTop(duration));
        changes.add(SlideViewChange.fromRight(duration));
        changes.add(SlideViewChange.fromBottom(duration));

        changes.add(AlphaViewChange.create(duration));

        return new ChangeControllerCollection(changes);
    }

    private final List<Change<ScreenKey>> changes;

    private ChangeControllerCollection(@NonNull List<Change<ScreenKey>> changes) {
        this.changes = changes;
    }

    @Nullable
    @Override
    public ChangeCallback forward(@NonNull ScreenManager<ScreenKey> manager, @Nullable Screen<ScreenKey, ? extends Parcelable> from, @NonNull Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {

        if (from == null) {
            return ChangeCallbackNoOp.noOp(endAction);
        }

        final ContentState state = (ContentState) from.state();
        final int index = (state.value() % changes.size());
        return changes.get(index).apply(false, manager, from, to, endAction);
    }

    @Nullable
    @Override
    public ChangeCallback back(@NonNull ScreenManager<ScreenKey> manager, @NonNull Screen<ScreenKey, ? extends Parcelable> from, @Nullable Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {

        if (to == null) {
            return ChangeCallbackNoOp.noOp(endAction);
        }

        final ContentState state = (ContentState) to.state();
        final int index = (state.value() % changes.size());
        return changes.get(index).apply(true, manager, to, from, endAction);
    }
}
