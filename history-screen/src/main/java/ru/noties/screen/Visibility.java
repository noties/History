package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public enum Visibility {

    VISIBLE(View.VISIBLE),
    INVISIBLE(View.INVISIBLE),
    GONE(View.GONE);

    private final int androidViewVisibility;

    Visibility(int androidViewVisibility) {
        this.androidViewVisibility = androidViewVisibility;
    }

    public int androidViewVisibility() {
        return androidViewVisibility;
    }

    public void apply(@NonNull View view) {
        view.setVisibility(androidViewVisibility);
    }

    /**
     * @param visibility visibility of a View (view.getVisibility())
     * @return {@link Visibility} or null if supplied value if not one of the View.{VISIBLE|INVISIBLE|GONE}
     */
    @SuppressWarnings("unused")
    @Nullable
    public static Visibility forValue(int visibility) {
        final Visibility out;
        if (View.GONE == visibility) {
            out = GONE;
        } else if (View.INVISIBLE == visibility) {
            out = INVISIBLE;
        } else if (View.VISIBLE == visibility) {
            out = VISIBLE;
        } else {
            out = null;
        }
        return out;
    }
}
