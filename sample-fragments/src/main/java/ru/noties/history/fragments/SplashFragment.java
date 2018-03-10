package ru.noties.history.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.Entry;

public class SplashFragment extends HistoryFragment<ScreenKey> {

    public static SplashFragment newInstance() {
        final Bundle bundle = new Bundle();

        final SplashFragment fragment = new SplashFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    // awful actually, but still
    private static final long DELAY = 2500L;

    private final Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, parent, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        handler.postDelayed(next, DELAY);
    }

    @Override
    public void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(null);
    }

    private final Runnable next = new Runnable() {
        @Override
        public void run() {
            history().replace(Entry.create(ScreenKey.MAIN_CONTENT, new MainContentState(0)));
        }
    };
}
