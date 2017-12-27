package ru.noties.history.screen;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.EnumMap;
import java.util.Map;

import ru.noties.listeners.Listeners;

class ScreenLifecycleImpl<K extends Enum<K>> implements ScreenLifecycleCallbacks<K>, ScreenLifecycle {


    private final Screen<K, ? extends Parcelable> screen;

    private final Map<Event, Listeners<Action>> eventActions = new EnumMap<Event, Listeners<Action>>(Event.class);


    ScreenLifecycleImpl(@NonNull Screen<K, ? extends Parcelable> screen) {
        this.screen = screen;
    }

    @Override
    public void init(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull ScreenManager<K> manager) {
        // no op
    }

    @Override
    public void destroy(@NonNull Screen<K, ? extends Parcelable> screen) {
        dispatch(screen, Event.DESTROY);
    }

    @Override
    public void onAttach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {
        dispatch(screen, Event.ATTACH);
    }

    @Override
    public void onDetach(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull View view) {
        dispatch(screen, Event.DETACH);
    }

    @Override
    public void onActive(@NonNull Screen<K, ? extends Parcelable> screen) {
        dispatch(screen, Event.ACTIVE);
    }

    @Override
    public void onInactive(@NonNull Screen<K, ? extends Parcelable> screen) {
        dispatch(screen, Event.INACTIVE);
    }

    @Override
    public void on(@NonNull Event event, @NonNull Action action) {
        Listeners<Action> actions = eventActions.get(event);
        if (actions == null) {
            actions = Listeners.create(3);
            eventActions.put(event, actions);
        }
        actions.add(action);
    }

    private void dispatch(@NonNull Screen<K, ? extends Parcelable> screen, @NonNull Event event) {

        final Listeners<Action> actions = this.screen == screen
                ? eventActions.get(event)
                : null;

        if (actions != null) {

            for (Action action : actions.begin()) {
                action.apply();
            }

            // these actions are one-shot
            actions.clear();
        }
    }
}
