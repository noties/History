package ru.noties.history.screen.plugin;

import android.support.annotation.NonNull;

public abstract class PermissionResultPlugin implements Plugin {

    public interface Action {

    }

    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return PermissionResultPlugin.class;
    }
}
