package ru.noties.screen.transition;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public class TransitionControllerBuilder<K extends Enum<K>> {

    private final Map<Key<K>, ScreenTransition<K>> transitions = new HashMap<>(3);

    private ScreenTransition<K> defaultTransition;

    @NonNull
    public TransitionControllerBuilder<K> when(@NonNull K from, @NonNull K to, @NonNull ScreenTransition<K> transition) {
        if (transitions.put(new Key<K>(from, to), transition) != null) {
            throw new IllegalStateException(String.format("Specified pair{from: %s, to: %s} already " +
                    "has registered transition", from, to));
        }
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> whenTo(@NonNull K to, @NonNull ScreenTransition<K> transition) {
        if (transitions.put(new Key<K>(null, to), transition) != null) {
            throw new IllegalStateException(String.format("Specified to: %s already has registered " +
                    "transition", to));
        }
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> whenFrom(@NonNull K from, @NonNull ScreenTransition<K> transition) {
        if (transitions.put(new Key<K>(from, null), transition) != null) {
            throw new IllegalStateException(String.format("Specified from: %s already has registered " +
                    "transition", from));
        }
        return this;
    }

    @NonNull
    public TransitionControllerBuilder<K> defaultTransition(@NonNull ScreenTransition<K> transition) {
        this.defaultTransition = transition;
        return this;
    }

    @NonNull
    public TransitionController<K> build() {
        if (defaultTransition == null) {
            defaultTransition = ScreenTransition.noOp();
        }
        return new Impl<>(transitions, defaultTransition);
    }

    private static class Key<K extends Enum<K>> {

        K from;
        K to;

        Key() {
        }

        Key(K from, K to) {
            this.from = from;
            this.to = to;
        }

        Key set(K from, K to) {
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

    private static class Impl<K extends Enum<K>> extends TransitionController<K> {

        private final Map<Key<K>, ScreenTransition<K>> transitions;
        private final ScreenTransition<K> defaultTransition;
        private final Key<K> lookup = new Key<>();

        private Impl(@NonNull Map<Key<K>, ScreenTransition<K>> transitions, @NonNull ScreenTransition<K> defaultTransition) {
            this.transitions = transitions;
            this.defaultTransition = defaultTransition;
        }

        @Nullable
        @Override
        public TransitionCallback forward(@Nullable Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return from == null
                    ? TransitionCallback.noOp(endAction)
                    : find(from.key, to.key).apply(false, from, to, endAction);
        }

        @Nullable
        @Override
        public TransitionCallback back(@NonNull Screen<K, ? extends Parcelable> from, @Nullable Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return to == null
                    ? TransitionCallback.noOp(endAction)
                    : find(to.key, from.key).apply(true, to, from, endAction);
        }

        @NonNull
        private ScreenTransition<K> find(@NonNull K from, @NonNull K to) {

            ScreenTransition<K> transition;

            transition = transitions.get(lookup.set(from, to));
            if (transition != null) {
                return transition;
            }

            transition = transitions.get(lookup.set(null, to));
            if (transition != null) {
                return transition;
            }

            transition = transitions.get(lookup.set(from, null));
            if (transition != null) {
                return transition;
            }

            return defaultTransition;
        }
    }
}
