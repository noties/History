package ru.noties.history.sample;

import android.support.annotation.NonNull;

import ru.noties.history.screen.plugin.Plugin;

public class ColorsPlugin implements Plugin {

    private final Colors colors;

    public ColorsPlugin(@NonNull Colors colors) {
        this.colors = colors;
    }

    @NonNull
    public Colors colors() {
        return colors;
    }

    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return ColorsPlugin.class;
    }
}
