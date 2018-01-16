package ru.noties.history.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.screen.BackPressedUtils;
import ru.noties.screen.ScreenLayout;
import ru.noties.screen.ScreenManager;
import ru.noties.screen.ScreenProvider;
import ru.noties.screen.plugin.ActivityResultPlugin;
import ru.noties.screen.plugin.OnBackPressedPlugin;
import ru.noties.screen.plugin.PermissionResultPlugin;
import ru.noties.screen.transit.Edge;

public class MainActivity extends Activity {

    // todo: theme swapping (by providing layout inflater or context) save/clear/restore
    // todo| viewPager... (mock add - no notification?)
    //      it's actually a good question: manual transition (via touch event for example)
    //      todo: first page of view pager is an empty page (transparent)
    // todo: visibility offset (dynamic) + maybe modify it in runtime (+ detach?)
    // todo: maybe manual transition? can we do that?

    private static final String KEY_STATE = "key.STATE";

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }


    private final OnBackPressedPlugin onBackPressedPlugin = OnBackPressedPlugin.create();

    private final ActivityResultPlugin activityResultPlugin = ActivityResultPlugin.create();

    private final PermissionResultPlugin permissionResultPlugin = PermissionResultPlugin.create();


    private final Colors colors = Colors.create();


    private ScreenManager<ScreenKey> screenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScreenLayout screenLayout = findViewById(R.id.screen_layout);
        final HistoryBar historyBar = findViewById(R.id.history_bar);

        final ScreenProvider<ScreenKey> screenProvider = ScreenProvider.builder(ScreenKey.class)
                .register(ScreenKey.CONTENT, ContentScreen::new)
                .build();

        final History<ScreenKey> history = History.create(ScreenKey.class);

        historyBar.setHistory(history);

        screenManager = ScreenManager.builder(history, screenProvider)
                .switchLock(screenLayout)
                .switchController(new AllSwitchController(Edge.RIGHT))
                .addPlugin(new ColorsPlugin(colors))
                .addPlugins(onBackPressedPlugin, activityResultPlugin, permissionResultPlugin)
                .build(this, screenLayout);

//        history.observe(new LoggingObserver<>());
//        screenManager.screenCallbacks(new LoggingScreenLifecycleCallbacks<>());

        if (!screenManager.restoreState(HistoryState.restore(savedInstanceState, KEY_STATE))) {
            history.push(Entry.create(ScreenKey.CONTENT, new ContentState(0, colors.next())));
        }
    }

    @Override
    public void onBackPressed() {
        if (!BackPressedUtils.onBackPressed(screenManager)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, screenManager.history().save());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!activityResultPlugin.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionResultPlugin.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
