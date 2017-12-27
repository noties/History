package ru.noties.history.screen;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.History;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Screen<K extends Enum<K>, S extends Parcelable> {

    @NonNull
    public final K key;

    @NonNull
    public final S state;


    private ScreenManager<K> manager;

    private View view;


    private boolean isActive;

    private boolean isDestroyed;


    public Screen(@NonNull K key, @NonNull S state) {
        this.key = key;
        this.state = state;
    }


    // this method should not modify parent (change number of children, manually attaching created view)
    @NonNull
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);


    @CallSuper
    public void init(@NonNull ScreenManager<K> manager) {
        this.manager = manager;
    }

    @CallSuper
    public void destroy() {
        this.isDestroyed = true;
        this.manager = null;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    @CallSuper
    public void onAttach(@NonNull View view) {
        this.view = view;
    }

    @CallSuper
    public void onDetach(@NonNull View view) {
        this.view = null;
    }

    public boolean isAttached() {
        return view != null;
    }

    @CallSuper
    public void onActive() {
        this.isActive = true;
    }

    @CallSuper
    public void onInactive() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    @NonNull
    public ScreenManager<K> manager() {
        return manager;
    }

    @NonNull
    public ScreenLifecycle lifecycle() {
        return manager.screenLifecycle(this);
    }

    @NonNull
    public Activity activity() {
        return manager.activity();
    }

    @NonNull
    public History<K> history() {
        return manager.history();
    }

    // deliberately not annotating it
    public View view() {
        return view;
    }
}
