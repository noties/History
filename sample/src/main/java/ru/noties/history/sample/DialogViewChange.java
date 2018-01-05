package ru.noties.history.sample;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.history.screen.change.SingleChange;
import ru.noties.history.screen.change.SingleViewChange;

public class DialogViewChange extends SingleViewChange {

    @NonNull
    public static <K extends Enum<K>> SingleChange<K> create(long duration) {
        //noinspection unchecked
        return new DialogViewChange(duration);
    }

    private final long duration;

    public DialogViewChange(long duration) {
        this.duration = duration;
    }

    @Override
    protected void applyStartValues(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        final Holder holder = holder(view);
        holder.background.setAlpha(reverse ? 1.F : .0F);
        holder.content.setTranslationY(reverse ? .0F : container.getHeight() - holder.content.getHeight());
    }

    @Override
    protected void startAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view, @NonNull Runnable endAction) {

        final Holder holder = holder(view);

        holder.background
                .animate()
                .setDuration(duration)
                .alpha(reverse ? .0F : 1.F)
                .start();

        holder.content
                .animate()
                .translationY(reverse ? container.getHeight() - holder.content.getHeight() : .0F)
                .setDuration(duration)
                .withEndAction(endAction)
                .start();
    }

    @Override
    protected void cancelAnimation(boolean reverse, @NonNull ViewGroup container, @NonNull View view) {
        final Holder holder = holder(view);
        holder.background.clearAnimation();
        holder.content.clearAnimation();
    }

    @NonNull
    private static Holder holder(@NonNull View view) {
        Holder holder = (Holder) view.getTag(R.id.holder);
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(R.id.holder, holder);
        }
        return holder;
    }

    private static class Holder {

        final View background;
        final View content;

        Holder(@NonNull View view) {
            this.background = view.findViewById(R.id.dialog_background);
            this.content = view.findViewById(R.id.dialog_content);
        }
    }
}
