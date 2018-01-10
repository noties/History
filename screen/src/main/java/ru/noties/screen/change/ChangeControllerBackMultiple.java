package ru.noties.screen.change;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

abstract class ChangeControllerBackMultiple {

    @NonNull
    static <K extends Enum<K>> ChangeCallback back(
            @NonNull ScreenManager<K> manager,
            @NonNull ChangeController<K> controller,
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    ) {
        // create pairs of screens to be animated
        //  only visible screens should participate
        return new CallbackImpl<>(manager, controller, extractPairs(from, to), endAction);
    }

    private static <K extends Enum<K>> List<Pair<K>> extractPairs(
            @NonNull List<Screen<K, ? extends Parcelable>> from,
            @NonNull Screen<K, ? extends Parcelable> to
    ) {

        final int fromSize = from.size();

        final List<Pair<K>> list = new ArrayList<>(fromSize);

        // last screen must be visible anyway
        Screen<K, ? extends Parcelable> previous = from.get(fromSize - 1);

        Screen<K, ? extends Parcelable> current;

        for (int i = fromSize - 2; i >= 0; i--) {
            current = from.get(i);
            list.add(new Pair<>(previous, current));
            previous = current;
        }

        list.add(new Pair<>(previous, to));

        return list;
    }

    private static class Pair<K extends Enum<K>> {

        final Screen<K, ? extends Parcelable> from;
        final Screen<K, ? extends Parcelable> to;

        Pair(@NonNull Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class CallbackImpl<K extends Enum<K>> implements ChangeCallback {

        private final ScreenManager<K> manager;
        private final ChangeController<K> controller;
        private final Deque<Pair<K>> pairs;
        private final Runnable endAction;
        private final Runnable action;

        private ChangeCallback pendingCallback;

        CallbackImpl(
                @NonNull ScreenManager<K> manager,
                @NonNull ChangeController<K> controller,
                @NonNull List<Pair<K>> pairs,
                @NonNull Runnable endAction
        ) {
            this.manager = manager;
            this.controller = controller;
            this.pairs = new ArrayDeque<>(pairs);
            this.endAction = endAction;
            this.action = new Runnable() {
                @Override
                public void run() {
                    // if pending callback is NULL it means that we are cancelled
                    if (pendingCallback != null) {
                        next();
                    }
                }
            };

            next();
        }

        private void next() {
            final Pair<K> pair = pairs.pollFirst();
            if (pair == null) {
                pendingCallback = null;
                endAction.run();
            } else {
                pendingCallback = controller.back(manager, pair.from, pair.to, action);
            }
        }

        @Override
        public void cancel() {
            final ChangeCallback changeCallback = pendingCallback;
            if (changeCallback != null) {
                pairs.clear();
                pendingCallback = null;
                changeCallback.cancel();
                endAction.run();
            }
        }
    }

    private ChangeControllerBackMultiple() {
    }
}
