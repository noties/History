package ru.noties.history.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import ru.noties.history.Entry;
import ru.noties.history.History;

public class MainActivity extends FragmentActivity implements HistoryProvider<ScreenKey> {

    private static final String KEY_HISTORY_STATE = "key.HistoryState";

    private HistoryManager<ScreenKey> historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.historyManager = HistoryManager.create(
                History.create(ScreenKey.class),
                new ScreenKeyProvider(),
                getSupportFragmentManager(),
                Window.ID_ANDROID_CONTENT
        );

        if (!historyManager.restore(savedInstanceState, KEY_HISTORY_STATE)) {
            historyManager.history().push(Entry.create(ScreenKey.SPLASH));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_HISTORY_STATE, historyManager.save());
    }

    @NonNull
    @Override
    public History<ScreenKey> provideHistory() {
        return historyManager.history();
    }

    @Override
    public void onBackPressed() {
        if (!historyManager.goBack()) {
            super.onBackPressed();
        }
    }
}
