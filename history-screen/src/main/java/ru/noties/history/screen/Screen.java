package ru.noties.history.screen;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.History;

/**
 * Lifecycle is simple:
 * <ul>
 * <li>init-destroy</li>
 * <li>onAttach-onDetach</li>
 * <li>onActive-onInactive</li>
 * <li></li>
 * </ul>
 */
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


    /**
     * Please note that one <strong>must</strong> not modify `parent` state. Do not attach created
     * view to it manually as it will be done by {@link ScreenManager}
     *
     * @param inflater LayoutInflater for convenience, it\'s not required to be used when creating a View
     * @param parent   ViewGroup to be attached to (do not attach manually).
     * @return View to be used
     * @see ScreenManagerBuilder#layoutInflater(LayoutInflater)
     */
    @NonNull
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    /**
     * This method will be called after {@link Screen} is built via constructor ({@link #Screen(Enum, Parcelable)}).
     *
     * @param manager {@link ScreenManager}
     * @see #destroy()
     */
    @CallSuper
    public void init(@NonNull ScreenManager<K> manager) {
        this.manager = manager;
    }

    /**
     * This is the last lifecycle event that this {@link Screen} will receive. It indicates that
     * {@link Screen} is to be destroyed.
     *
     * @see #init(ScreenManager)
     */
    @CallSuper
    public void destroy() {
        this.isDestroyed = true;
        this.manager = null;
    }

    /**
     * @return a flag indicating if this {@link Screen} went through destroy (and must not be used,
     * released from referencing)
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * @param view View that was returned from {@link #onCreateView(LayoutInflater, ViewGroup)} and is
     *             attached to layout
     * @see #onDetach(View)
     */
    @CallSuper
    public void onAttach(@NonNull View view) {
        this.view = view;
    }

    /**
     * @param view View to clear before it is removed from layout
     * @see #onAttach(View)
     */
    @CallSuper
    public void onDetach(@NonNull View view) {
        this.view = null;
    }

    /**
     * @return a flag indicating if this {@link Screen} is attached to a layout (has View)
     */
    public boolean isAttached() {
        return view != null;
    }

    /**
     * This {@link Screen} is active one (the top). No 2 screens can be active at a time.
     * Also, a {@link Screen} can be active only if underlying activity is in `resumed` state
     */
    @CallSuper
    public void onActive() {
        this.isActive = true;
    }

    /**
     * This {@link Screen} is not active anymore. Can happen if another screen becomes active or
     * underlying activity went through `onPause`
     */
    @CallSuper
    public void onInactive() {
        this.isActive = false;
    }

    /**
     * @return a flag indicating if this {@link Screen} is currently active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return underlying {@link ScreenManager}
     */
    @NonNull
    public ScreenManager<K> manager() {
        return manager;
    }

    /**
     * @return {@link ScreenLifecycle}
     * @see ScreenManager#screenLifecycle(Screen)
     */
    @NonNull
    public ScreenLifecycle lifecycle() {
        return manager.screenLifecycle(this);
    }

    /**
     * @return underlying Activity (present all the time until {@link Screen} is destroyed.
     * @see ScreenManager#activity()
     */
    @NonNull
    public Activity activity() {
        return manager.activity();
    }

    /**
     * @return underlying {@link History}
     * @see ScreenManager#history()
     */
    @NonNull
    public History<K> history() {
        return manager.history();
    }

    /**
     * @return View if it\'s present ({@link Screen} is attached)
     */
    public View view() {
        return view;
    }
}
