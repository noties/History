package ru.noties.history.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import ru.noties.history.Entry;

public class ScreenKeyProvider implements HistoryManager.Provider<ScreenKey> {

    @NonNull
    @Override
    public Fragment fragment(@NonNull Entry<ScreenKey> entry) {

        final Fragment fragment;

        switch (entry.key()) {

            case SPLASH:
                fragment = SplashFragment.newInstance();
                break;

            case MAIN_CONTENT:
                fragment = MainContentFragment.newInstance((MainContentState) entry.state());
                break;

            default:
                throw new RuntimeException("Unexpected ScreenKey: " + entry.key());
        }

        return fragment;
    }
}
