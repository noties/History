package ru.noties.history.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;
import ru.noties.screen.BackPressedUtils;
import ru.noties.screen.Screen;
import ru.noties.screen.ScreenLayout;
import ru.noties.screen.ScreenManager;
import ru.noties.screen.ScreenProvider;
import ru.noties.screen.Visibility;
import ru.noties.screen.VisibilityProvider;
import ru.noties.screen.plugin.ActivityResultPlugin;
import ru.noties.screen.plugin.OnBackPressedPlugin;
import ru.noties.screen.plugin.PermissionResultPlugin;
import ru.noties.screen.transit.ScreenSwitch;
import ru.noties.screen.transit.SwitchController;
import ru.noties.screen.transit.SwitchEngine;
import ru.noties.screen.transit.SwitchEngineCallback;
import ru.noties.screen.transit.ValueAnimatorEngine;

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
                .visibilityProvider(VisibilityProvider.create(Visibility.VISIBLE))
                .switchController(new Controller())
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

    private static class Controller extends SwitchController<ScreenKey> {

        private final List<SwitchEngine<ScreenKey>> list;

        Controller() {
            list = new ArrayList<>(2);
            list.add(ValueAnimatorEngine.create(new Half(false), 250L));
            list.add(ValueAnimatorEngine.create(new Half(true), 250L));
        }

        @Nullable
        @Override
        protected SwitchEngineCallback apply(
                boolean reverse,
                @NonNull Screen<ScreenKey, ? extends Parcelable> from,
                @NonNull Screen<ScreenKey, ? extends Parcelable> to,
                @NonNull Runnable endAction
        ) {
            final ContentState state = (ContentState) from.state();
            return list.get(state.value() % list.size()).apply(reverse, from, to, endAction);
        }

        @NonNull
        @Override
        public SwitchEngine<ScreenKey> switchEngine(
                @NonNull Screen<ScreenKey, ? extends Parcelable> from,
                @NonNull Screen<ScreenKey, ? extends Parcelable> to
        ) {
            final ContentState state = (ContentState) from.state();
            return list.get(state.value() % list.size());
        }
    }

    private static class Half extends ScreenSwitch<ScreenKey> {

        private final boolean vertical;

        Half(boolean vertical) {
            this.vertical = vertical;
        }

        @Override
        public void apply(
                float fraction,
                @NonNull Screen<ScreenKey, ? extends Parcelable> from,
                @NonNull Screen<ScreenKey, ? extends Parcelable> to
        ) {

            final View fromView = from.view();
            final View toView = to.view();

            if (vertical) {

                fromView.setTranslationY(-.5F * fraction * fromView.getHeight());

                final int toHeight = toView.getHeight();
                toView.setTranslationY((toHeight / 2) + (.5F * (1.F - fraction) * toHeight));
            } else {

                fromView.setTranslationX(-.5F * fraction * fromView.getWidth());

                final int toWidth = toView.getWidth();
                toView.setTranslationX((toWidth / 2) + (.5F * (1.F - fraction) * toWidth));
            }
        }
    }
}
