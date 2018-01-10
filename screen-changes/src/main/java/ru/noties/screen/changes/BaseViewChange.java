package ru.noties.screen.changes;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.screen.change.ViewChange;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.ViewTweenManager;

abstract class BaseViewChange extends ViewChange {

    @NonNull
    static TweenManager tweenManager(@NonNull ViewGroup container) {
        return ViewTweenManager.get(R.id.changes_tumbleweed_manager, container);
    }

    void resetPivot(@NonNull View from, @NonNull View to) {

        from.setPivotX(from.getWidth() / 2);
        from.setPivotY(from.getHeight() / 2);

        to.setPivotX(to.getWidth() / 2);
        to.setPivotY(to.getHeight() / 2);
    }
}
