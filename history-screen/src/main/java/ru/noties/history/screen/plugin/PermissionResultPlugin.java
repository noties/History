package ru.noties.history.screen.plugin;

import android.support.annotation.NonNull;

public abstract class PermissionResultPlugin implements Plugin {



    @NonNull
    @Override
    public Class<? extends Plugin> pluginType() {
        return PermissionResultPlugin.class;
    }
}
