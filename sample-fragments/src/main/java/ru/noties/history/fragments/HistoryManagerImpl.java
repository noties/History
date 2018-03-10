package ru.noties.history.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryState;

class HistoryManagerImpl<K extends Enum<K>> extends HistoryManager<K> implements History.Observer<K> {

    private final History<K> history;
    private final Provider<K> provider;
    private final FragmentManager fragmentManager;
    private final int containerId;

    private boolean isRestoring;

    HistoryManagerImpl(
            @NonNull History<K> history,
            @NonNull Provider<K> provider,
            @NonNull FragmentManager fragmentManager,
            @IdRes int containerId
    ) {
        this.history = history;
        this.provider = provider;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;

        this.history.observe(this);
    }

    @Override
    public boolean goBack() {
        return history.pop();
    }

    @NonNull
    @Override
    public History<K> history() {
        return history;
    }

    @NonNull
    @Override
    public HistoryState save() {
        return history.save();
    }

    @Override
    public boolean restore(@Nullable HistoryState state) {
        this.isRestoring = true;
        final boolean result = history.restore(state);
        this.isRestoring = false;
        if (result) {

            final Entry<K> last = history.last();
            if (last != null) {
                final String tag = tag(last);
                if (fragmentManager.findFragmentByTag(tag) == null) {
                    fragmentManager.beginTransaction()
                            .replace(containerId, provider.fragment(last), tag)
                            .commitNow();
                }
            } else {
                throw new RuntimeException("Unexpected state, there is no last Entry in History " +
                        "after state restoration");
            }
        }
        return result;
    }

    @Override
    public boolean restore(@Nullable Bundle savedInstanceState, @NonNull String key) {
        return restore(HistoryState.restore(savedInstanceState, key));
    }

    @Override
    public void onEntryPushed(@Nullable Entry<K> previous, @NonNull Entry<K> current) {

        if (isRestoring) {
            return;
        }

        // first view, no animation, no transitions, nothing, just show it
        if (previous == null) {

            fragmentManager.beginTransaction()
                    .replace(containerId, provider.fragment(current), tag(current))
                    .commitNow();

        } else {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            try {

                final Fragment from = fragmentManager.findFragmentByTag(tag(previous));
                final Fragment to = provider.fragment(current);

                // pass for configuration (if needed)

                transaction
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .remove(from)
                        .replace(containerId, to, tag(current));

            } finally {
                transaction.commitNow();
            }
        }
    }

    @Override
    public void onEntryReplaced(@Nullable Entry<K> previous, @NonNull Entry<K> current) {
        onEntryPushed(previous, current);
    }

    @Override
    public void onEntryPopped(@NonNull Entry<K> popped, @Nullable Entry<K> toAppear) {

        if (toAppear == null) {
            // nothing to do here, last screen
            return;
        }

        final Fragment from = fragmentManager.findFragmentByTag(tag(popped));
        final Fragment to = provider.fragment(toAppear);

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        try {
            transaction
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(from)
                    .replace(containerId, to, tag(toAppear));
        } finally {
            transaction.commitNow();
        }
    }

    @Override
    public void onEntriesPopped(@NonNull List<Entry<K>> popped, @Nullable Entry<K> toAppear) {

        if (toAppear == null) {
            // all fragment were removed, do not do anything
            return;
        }

        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, provider.fragment(toAppear), tag(toAppear))
                .commitNow();
    }

    @NonNull
    private static String tag(@NonNull Entry<? extends Enum<?>> entry) {
        return "history:fragments:entry:" + entry.id();
    }
}
