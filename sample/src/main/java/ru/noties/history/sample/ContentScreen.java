package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.noties.history.Entry;
import ru.noties.screen.Screen;
import ru.noties.screen.ScreenManager;

public class ContentScreen extends Screen<ScreenKey, ContentState> {

    private Colors colors;

    public ContentScreen(@NonNull ScreenKey key, @NonNull ContentState state) {
        super(key, state);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.screen_content, parent, false);
    }

    @Override
    public void init(@NonNull ScreenManager<ScreenKey> manager) {
        super.init(manager);

        colors = manager.plugin(ColorsPlugin.class).colors();
    }

    @Override
    public void onAttach(@NonNull View view) {
        super.onAttach(view);

        view.setBackgroundColor(state.color());

        final TextView textView = view.findViewById(R.id.text_view);
        textView.setText(textView.getResources().getString(R.string.content_pattern, state.value()));

        view.findViewById(R.id.content_button_push)
                .setOnClickListener(v -> history().push(Entry.create(ScreenKey.CONTENT, next())));

        view.findViewById(R.id.content_button_replace)
                .setOnClickListener(v -> history().replace(Entry.create(ScreenKey.CONTENT, next())));

        view.findViewById(R.id.content_button_pop)
                .setOnClickListener(v -> activity().onBackPressed());
    }

    @NonNull
    private ContentState next() {
        return new ContentState(state.value() + 1, colors.next());
    }
}
