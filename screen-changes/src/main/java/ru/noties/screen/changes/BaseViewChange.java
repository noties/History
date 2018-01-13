package ru.noties.screen.changes;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.screen.change.ViewChange;
import ru.noties.tumbleweed.TweenManager;
import ru.noties.tumbleweed.android.ViewTweenManager;

@SuppressWarnings("WeakerAccess")
public abstract class BaseViewChange extends ViewChange {

    protected static final boolean IS_M = Build.VERSION.SDK_INT == Build.VERSION_CODES.M;

    @NonNull
    static TweenManager tweenManager(@NonNull ViewGroup container) {
        return ViewTweenManager.get(R.id.changes_tumbleweed_manager, container);
    }

    @Override
    protected void cancelChange(boolean reverse, @NonNull ViewGroup container, @NonNull View from, @NonNull View to) {
        tweenManager(container).killAll();
    }

    protected void resetPivot(@NonNull View from, @NonNull View to) {

        from.setPivotX(from.getWidth() / 2);
        from.setPivotY(from.getHeight() / 2);

        to.setPivotX(to.getWidth() / 2);
        to.setPivotY(to.getHeight() / 2);
    }

    // on good ol' mighty Android M, some scaling/rotating animations are failing with DeadObjectException
    // as a work_around applying LAYER_TYPE_SOFTWARE helps
    protected void androidMBeforeScaling(@NonNull View view) {
        if (IS_M) {
            final int currentType = view.getLayerType();
            if (View.LAYER_TYPE_SOFTWARE != currentType) {
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                view.setTag(R.id.m_layer_type, currentType);
            }
        }
    }

    protected void androidMAfterScaling(@NonNull View view) {
        if (IS_M) {
            final Integer previousType = (Integer) view.getTag(R.id.m_layer_type);
            if (previousType != null) {
                view.setLayerType(previousType, null);
            }
        }
    }
}
