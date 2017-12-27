package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.debug.Debug;
import ru.noties.history.EmptyState;
import ru.noties.history.Entry;
import ru.noties.history.screen.Screen;
import ru.noties.history.screen.ScreenLifecycle;
import ru.noties.history.screen.ScreenManager;
import ru.noties.history.screen.plugin.ActivityResultPlugin;
import ru.noties.requirements.EventSource;
import ru.noties.requirements.Payload;
import ru.noties.requirements.Requirement;
import ru.noties.requirements.Requirement.Listener;
import ru.noties.requirements.RequirementBuilder;

public class RequirementScreen extends Screen<ScreenKey, EmptyState> implements Listener {


    private Requirement requirement;

    private EventSource eventSource = EventSource.create();


    public RequirementScreen(@NonNull ScreenKey key, @NonNull EmptyState state) {
        super(key, state);
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.screen_requirement, parent, false);
    }

    @Override
    public void init(@NonNull ScreenManager<ScreenKey> manager) {
        super.init(manager);

        requirement = RequirementBuilder.create()
                .add(new BluetoothRequirementCase())
                .build(manager.activity(), eventSource);

        manager.plugin(ActivityResultPlugin.class)
                .observeAll(eventSource::onActivityResult)
                .accept(s -> lifecycle().on(ScreenLifecycle.Event.DESTROY, s::unsubscribe));
    }

    @Override
    public void onAttach(@NonNull View view) {
        super.onAttach(view);

        view.findViewById(R.id.button)
                .setOnClickListener(v -> requirement.validate(this));
    }

    @Override
    public void onRequirementSuccess() {
        history().replace(Entry.create(ScreenKey.START, new StartState(77)));
    }

    @Override
    public void onRequirementFailure(@Nullable Payload payload) {
        // no op
        Debug.i("payload: %s", payload);
    }
}
