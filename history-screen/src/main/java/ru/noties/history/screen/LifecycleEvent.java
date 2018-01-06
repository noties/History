package ru.noties.history.screen;

// we earliest we can request a lifecycle event triggered is in INIT, so it cannot be here
public enum LifecycleEvent {
    ATTACH,
    DETACH,
    ACTIVE,
    INACTIVE,
    DESTROY
}
