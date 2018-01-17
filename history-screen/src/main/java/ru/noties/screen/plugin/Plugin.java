package ru.noties.screen.plugin;

import android.support.annotation.NonNull;

public interface Plugin {

    @NonNull
    Class<? extends Plugin> pluginType();
}
