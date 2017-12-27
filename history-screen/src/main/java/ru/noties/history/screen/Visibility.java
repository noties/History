package ru.noties.history.screen;

import android.support.annotation.NonNull;
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
}
