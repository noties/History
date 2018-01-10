package ru.noties.history.sample;

import android.app.Activity;
import android.os.Bundle;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.screen.ScreenLayout;
import ru.noties.screen.ScreenManager;
import ru.noties.screen.ScreenProvider;
import ru.noties.screen.Visibility;
import ru.noties.screen.VisibilityProvider;
import ru.noties.screen.change.ChangeController;
import ru.noties.screen.change.ViewChangeSlide;

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
                .changeController(ChangeController.<ScreenKey>create(ViewChangeSlide.fromRight(250L)))
                .visibilityProvider(VisibilityProvider.create(Visibility.VISIBLE))
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
}
