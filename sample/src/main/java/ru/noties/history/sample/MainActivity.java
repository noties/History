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
import ru.noties.history.screen.ScreenLayout;
import ru.noties.history.screen.ScreenManager;
import ru.noties.history.screen.ScreenProvider;
import ru.noties.history.screen.Visibility;
import ru.noties.history.screen.VisibilityProvider;
import ru.noties.history.screen.change.ChangeController;
import ru.noties.history.screen.change.SingleViewChangeSlide;
import ru.noties.history.screen.change.ViewChangeAlpha;
import ru.noties.history.screen.change.ViewChangeSlide;
import ru.noties.history.screen.plugin.ActivityResultPlugin;
import ru.noties.requirements.EventSource;

public class MainActivity extends Activity {

    // todo: theme swapping (by providing layout inflater or context) save/clear/restore
    // todo: prebuilt history (History.builder() ?) skip layout creation...

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    private History<ScreenKey> history;
    private ScreenManager<ScreenKey> screenManager;

    private final ActivityResultPlugin activityResultPlugin = ActivityResultPlugin.create();

    private EventSource eventSource = EventSource.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // todo| viewPager... (mock add - no notification?)
        //      it's actually a good question: manual transition (via touch event for example)

        final ScreenProvider<ScreenKey> screenProvider = ScreenProvider.builder(ScreenKey.class)
                .register(ScreenKey.SPLASH, SplashScreen::new)
                .register(ScreenKey.START, StartScreen::new)
                .register(ScreenKey.REQUIREMENT, RequirementScreen::new)
                .register(ScreenKey.DIALOG, DialogScreen::new)
                .build();

//        final TransitionController<ScreenKey> transitionController = TransitionController.builder(ScreenKey.class)
//                .when(ScreenKey.SPLASH, ScreenKey.START, SlideTransition.top(200), SlideTransition.bottom(200))
//                .whenTo(ScreenKey.START, SlideTransition.right(200), SlideTransition.left(200))
//                .when(ScreenKey.START, ScreenKey.REQUIREMENT, SlideTransition.bottom(200), SlideTransition.top(200))
//                .when(ScreenKey.REQUIREMENT, ScreenKey.START, new AlphaTransition(200), new AlphaTransition(200))
//                .whenTo(ScreenKey.DIALOG, new AlphaTransition(200), SlideTransition.left(200))
//                .build();

        final ChangeController<ScreenKey> changeController = ChangeController.builder(ScreenKey.class)
                .when(ScreenKey.SPLASH, ScreenKey.START, ViewChangeAlpha.create(250L))
                .whenTo(ScreenKey.START, ViewChangeSlide.fromBottom(250L))
                .whenTo(ScreenKey.DIALOG, SingleViewChangeSlide.toLeft(250L), SingleViewChangeSlide.fromRight(250L))
                .build();

        final VisibilityProvider<ScreenKey> visibilityProvider = VisibilityProvider.builder(ScreenKey.class)
                .whenTo(ScreenKey.DIALOG, Visibility.VISIBLE)
                .defaultVisibility(null)
                .build();

        history = History.create(ScreenKey.class);

        final ScreenLayout screenLayout = findViewById(R.id.screen_layout);

        screenManager = ScreenManager.builder(history, screenProvider)
                .visibilityProvider(visibilityProvider)
                .changeController(changeController)
                .addPlugin(activityResultPlugin)
                .changeLock(screenLayout)
                .build(this, screenLayout);

        history.observe(new LoggingObserver<ScreenKey>());
        screenManager.screenCallbacks(new LoggingScreenLifecycleCallbacks<>());

        if (!screenManager.restoreState(HistoryState.restore(savedInstanceState, "key"))) {
            history.push(Entry.create(ScreenKey.SPLASH));
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
        outState.putParcelable("key", screenManager.history().save());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultPlugin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!eventSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
