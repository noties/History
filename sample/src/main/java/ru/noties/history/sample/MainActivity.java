package ru.noties.history.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.screen.Screen;
import ru.noties.screen.ScreenLayout;
import ru.noties.screen.ScreenManager;
import ru.noties.screen.ScreenProvider;
import ru.noties.screen.change.Change;
import ru.noties.screen.change.ChangeCallback;
import ru.noties.screen.change.ChangeCallbackNoOp;
import ru.noties.screen.change.ChangeController;
import ru.noties.screen.changes.AccordionViewChange;
import ru.noties.screen.changes.CubeOutViewChange;
import ru.noties.screen.changes.DepthViewChange;
import ru.noties.screen.changes.FlipViewChange;
import ru.noties.screen.changes.ParallaxViewChange;
import ru.noties.screen.changes.ZoomOutViewChange;

public class MainActivity extends Activity {

    // todo: theme swapping (by providing layout inflater or context) save/clear/restore
    // todo: prebuilt history (History.builder() ?) skip layout creation...
    // todo| viewPager... (mock add - no notification?)
    //      it's actually a good question: manual transition (via touch event for example)
    //      todo: first page of view pager is an empty page (transparent)
    // todo: visibility offset (dynamic) + maybe modify it in runtime (+ detach?)
    // todo: maybe manual transition? can we do that?

    // todo: what if we will call visibility provider for each entry? this way it would be easy to
    //      implement offset (so, no more than certain amount of views are hold in layout)

    private static final String KEY_STATE = "key.STATE";

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    private final Colors colors = Colors.create();

    private ScreenManager<ScreenKey> screenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScreenProvider<ScreenKey> screenProvider = ScreenProvider.builder(ScreenKey.class)
                .register(ScreenKey.CONTENT, ContentScreen::new)
                .build();

        final History<ScreenKey> history = History.create(ScreenKey.class);

        final ScreenLayout screenLayout = findViewById(R.id.screen_layout);
        final HistoryBar historyBar = findViewById(R.id.history_bar);
        historyBar.setHistory(history);

        screenManager = ScreenManager.builder(history, screenProvider)
                .changeLock(screenLayout)
                .changeController(new ChangeControllerImpl(createChanges()))
                .addPlugin(new ColorsPlugin(colors))
                .build(this, screenLayout);

//        history.observe(new LoggingObserver<>());
//        screenManager.screenCallbacks(new LoggingScreenLifecycleCallbacks<>());

        if (!screenManager.restoreState(HistoryState.restore(savedInstanceState, KEY_STATE))) {
            history.push(Entry.create(ScreenKey.CONTENT, new ContentState(0, colors.next())));
        }
    }

    @Override
    public void onBackPressed() {
        if (!screenManager.history().pop()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, screenManager.history().save());
    }

    @NonNull
    private static List<Change<ScreenKey>> createChanges() {

        final long duration = 250L;

        final List<Change<ScreenKey>> changes = new ArrayList<>();

        changes.add(CubeOutViewChange.fromLeft(duration));
        changes.add(CubeOutViewChange.fromTop(duration));
        changes.add(CubeOutViewChange.fromRight(duration));
        changes.add(CubeOutViewChange.fromBottom(duration));

        changes.add(DepthViewChange.fromLeft(duration));
        changes.add(DepthViewChange.fromTop(duration));
        changes.add(DepthViewChange.fromRight(duration));
        changes.add(DepthViewChange.fromBottom(duration));

        changes.add(ZoomOutViewChange.fromLeft(duration * 3));
        changes.add(ZoomOutViewChange.fromTop(duration * 3));
        changes.add(ZoomOutViewChange.fromRight(duration * 3));
        changes.add(ZoomOutViewChange.fromBottom(duration * 3));

        changes.add(AccordionViewChange.fromLeft(duration));
        changes.add(AccordionViewChange.fromTop(duration));
        changes.add(AccordionViewChange.fromRight(duration));
        changes.add(AccordionViewChange.fromBottom(duration));

        changes.add(FlipViewChange.fromLeft(duration * 2));
        changes.add(FlipViewChange.fromTop(duration * 2));
        changes.add(FlipViewChange.fromRight(duration * 2));
        changes.add(FlipViewChange.fromBottom(duration * 2));

        changes.add(ParallaxViewChange.fromLeft(duration));
        changes.add(ParallaxViewChange.fromTop(duration));
        changes.add(ParallaxViewChange.fromRight(duration));
        changes.add(ParallaxViewChange.fromBottom(duration));

        return changes;
    }

    private static class ChangeControllerImpl extends ChangeController<ScreenKey> {

        private final List<Change<ScreenKey>> changes;

        private ChangeControllerImpl(@NonNull List<Change<ScreenKey>> changes) {
            this.changes = changes;
        }

        @NonNull
        @Override
        public ChangeCallback forward(@NonNull ScreenManager<ScreenKey> manager, @Nullable Screen<ScreenKey, ? extends Parcelable> from, @NonNull Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {

            if (from == null) {
                return ChangeCallbackNoOp.noOp(endAction);
            }

            final ContentState state = (ContentState) from.state;
            final int index = (state.value() % changes.size());
            return changes.get(index).apply(false, manager, from, to, endAction);
        }

        @NonNull
        @Override
        public ChangeCallback back(@NonNull ScreenManager<ScreenKey> manager, @NonNull Screen<ScreenKey, ? extends Parcelable> from, @Nullable Screen<ScreenKey, ? extends Parcelable> to, @NonNull Runnable endAction) {

            if (to == null) {
                return ChangeCallbackNoOp.noOp(endAction);
            }

            final ContentState state = (ContentState) to.state;
            final int index = (state.value() % changes.size());
            return changes.get(index).apply(true, manager, to, from, endAction);
        }
    }
}
