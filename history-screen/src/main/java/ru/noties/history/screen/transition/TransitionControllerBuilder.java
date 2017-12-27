package ru.noties.history.screen.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import ru.noties.history.Entry;

public class TransitionControllerBuilder<K extends Enum<K>> {

    @NonNull
    public TransitionControllerBuilder<K> when(@NonNull K from, @NonNull K to, @NonNull TransitionController<K> controller) {
        final Key<K> key = new Key<>(from, to);
        if (controllers.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified from: %s, to: %s keys already " +
                    "have controller registered", from, to));
        }
        controllers.put(key, controller);
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> when(@NonNull K from, @NonNull K to, @NonNull Transition forward, @NonNull Transition back) {
        when(from, to, create(from, forward, back));
        return this;
    }

    // trigger when to is requested
    @NonNull
    public TransitionControllerBuilder<K> whenTo(@NonNull K to, @NonNull TransitionController<K> controller) {
        final Key<K> key = new Key<>(null, to);
        if (controllers.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified to key: %s already has registered controller", to));
        }
        controllers.put(key, controller);
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> whenTo(@NonNull K to, @NonNull Transition forward, @NonNull Transition back) {
        whenTo(to, create(to, forward, back));
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> whenFrom(@NonNull K from, @NonNull TransitionController<K> controller) {
        final Key<K> key = new Key<>(from, null);
        if (controllers.containsKey(key)) {
            throw new IllegalStateException(String.format("Specified from key: %s already has registered controller", from));
        }
        controllers.put(key, controller);
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> whenFrom(@NonNull K from, @NonNull Transition forward, @NonNull Transition back) {
        whenFrom(from, create(from, forward, back));
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> defaultTransitions(@NonNull Transition forward, @NonNull Transition back) {
        this.defaultTransitionController = TransitionController.create(forward, back);
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> defaultTransitions(@NonNull TransitionController<K> controller) {
        this.defaultTransitionController = controller;
        return this;
    }

    @NonNull
    public TransitionController<K> build() {

        if (isBuilt) {
            throw new IllegalStateException("This instance of TransitionControllerBuilder has " +
                    "already been built");
        }

        isBuilt = true;

        final TransitionController<K> controller;
        if (defaultTransitionController == null) {
            controller = TransitionController.create(TransitionNoOp.instance(), TransitionNoOp.instance());
        } else {
            controller = defaultTransitionController;
        }

        return new Impl<>(controllers, controller);
    }


    private final Map<Key<K>, TransitionController<K>> controllers = new HashMap<>(3);

    private TransitionController<K> defaultTransitionController;

    private boolean isBuilt;

    TransitionControllerBuilder() {
    }

    @NonNull
    private static <K extends Enum<K>> TransitionController<K> create(@NonNull K k, @NonNull Transition forward, @NonNull Transition back) {
        return TransitionController.create(forward, back);
    }

    private static class Impl<K extends Enum<K>> extends TransitionController<K> {

        // transitions priority
        //      -> exact match FROM & TO
        //      -> matched TO
        //      -> matched FROM
        //      -> default transition

        private final Map<Key<K>, TransitionController<K>> controllers;

        private final TransitionController<K> defaultController;

        // lookup key
        private final Key<K> key = new Key<>();

        private Impl(@NonNull Map<Key<K>, TransitionController<K>> controllers, @NonNull TransitionController<K> defaultController) {
            this.controllers = controllers;
            this.defaultController = defaultController;
        }

        @NonNull
        @Override
        public Transition.Callback forward(
                @Nullable Entry<K> from,
                @NonNull Entry<K> to,
                @Nullable View fromView,
                @NonNull View toView,
                @NonNull Runnable endAction
        ) {
            return from != null && fromView != null
                    ? find(from.key(), to.key()).forward(from, to, fromView, toView, endAction)
                    : defaultController.forward(from, to, fromView, toView, endAction);
        }

        @NonNull
        @Override
        public Transition.Callback back(
                @NonNull Entry<K> from,
                @Nullable Entry<K> to,
                @NonNull View fromView,
                @Nullable View toView,
                @NonNull Runnable endAction
        ) {
            return to != null && toView != null
                    ? find(to.key(), from.key()).back(from, to, fromView, toView, endAction)
                    : defaultController.back(from, to, fromView, toView, endAction);
        }

        @NonNull
        private TransitionController<K> find(@NonNull K from, @NonNull K to) {

            TransitionController<K> controller = controllers.get(key.set(from, to));
            if (controller != null) {
                return controller;
            }

            controller = controllers.get(key.set(null, to));
            if (controller != null) {
                return controller;
            }

            controller = controllers.get(key.set(from, to));
            if (controller != null) {
                return controller;
            }

            return defaultController;
        }
    }

    private static class Key<K extends Enum<K>> {

        K from;
        K to;

        Key(K from, K to) {
            this.from = from;
            this.to = to;
        }

        Key() {
        }

        Key<K> set(K from, K to) {
            this.from = from;
            this.to = to;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key<?> key = (Key<?>) o;

            if (from != null ? !from.equals(key.from) : key.from != null) return false;
            return to != null ? to.equals(key.to) : key.to == null;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }
}
