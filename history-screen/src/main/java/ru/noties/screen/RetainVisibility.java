package ru.noties.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public enum RetainVisibility {

    INVISIBLE(View.INVISIBLE),
    GONE(View.GONE);

    private final int androidViewVisibility;

    RetainVisibility(int androidViewVisibility) {
        this.androidViewVisibility = androidViewVisibility;
    }

    public void apply(@NonNull View view) {
        view.setVisibility(androidViewVisibility);
    }

    /**
     * @param visibility visibility of a View (view.getVisibility())
     * @return {@link RetainVisibility} or null if supplied value if not one of the View.{INVISIBLE|GONE}
     */
    @SuppressWarnings("unused")
    @Nullable
    public static RetainVisibility forValue(int visibility) {
        final RetainVisibility out;
        if (View.GONE == visibility) {
            out = GONE;
        } else if (View.INVISIBLE == visibility) {
            out = INVISIBLE;
        } else {
            out = null;
        }
        return out;
    }
}
