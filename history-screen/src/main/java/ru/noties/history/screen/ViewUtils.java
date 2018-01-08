package ru.noties.history.screen;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ViewUtils {

    public interface WhenReady {
        void apply();
    }

    public static boolean isReady(@NonNull View view) {
        return view.getWidth() > 0;
    }

    /**
     * Non-blocking listening for the View state. Registers `OnPreDrawListener` but returns `true`
     * to process with drawing even if View is not in layout (visible)
     *
     * @param view      android.view.View to listen for state
     * @param whenReady {@link WhenReady} callback
     * @see #whenReadyBlocking(View, WhenReady)
     */
    public static void whenReady(@NonNull final View view, @NonNull final WhenReady whenReady) {
        if (!isReady(view)) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (isReady(view)) {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        whenReady.apply();
                    }
                    return true;
                }
            });
            view.invalidate();
        } else {
            whenReady.apply();
        }
    }

    /**
     * Blocking way to listen for a View state. No drawing will occur before a View is ready to be drawn.
     * This possibly can lead for the whole layout to **not** be drawn (not this specific view only)
     *
     * @param view      android.view.View to listen for state
     * @param whenReady {@link WhenReady} callback
     * @see #whenReady(View, WhenReady)
     */
    public static void whenReadyBlocking(@NonNull final View view, @NonNull final WhenReady whenReady) {
        if (!isReady(view)) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (isReady(view)) {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        whenReady.apply();
                        return true;
                    }
                    return false;
                }
            });
            view.invalidate();
        } else {
            whenReady.apply();
        }
    }

    private ViewUtils() {
    }
}
