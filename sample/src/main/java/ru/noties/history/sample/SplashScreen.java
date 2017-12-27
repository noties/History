package ru.noties.history.sample;

import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.Entry;
import ru.noties.history.screen.Screen;

public class SplashScreen extends Screen<ScreenKey, Parcelable> {

    private final Handler handler = new Handler();

    public SplashScreen(@NonNull ScreenKey key, @NonNull Parcelable state) {
        super(key, state);
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.screen_splash, parent, false);
    }

    @Override
    public void onActive() {
        super.onActive();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                history().replace(Entry.create(ScreenKey.START, new StartState(0)));
            }
        }, 1500L);
    }

    @Override
    public void onInactive() {
        super.onInactive();

        handler.removeCallbacksAndMessages(null);
    }
}
