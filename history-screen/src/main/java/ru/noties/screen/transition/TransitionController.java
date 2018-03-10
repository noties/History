package ru.noties.screen.transition;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.screen.Screen;

@SuppressWarnings("unused")
public abstract class TransitionController<K extends Enum<K>> {

    /**
     * Factory method to create a {@link TransitionController} that contains only one {@link ScreenTransition}
     * @param transition {@link ScreenTransition}
     * @return {@link TransitionController}
     */
    @NonNull
    public static <K extends Enum<K>> TransitionController<K> create(@NonNull ScreenTransition<K> transition) {
        return new Impl<>(transition);
    }

    /**
     * Factory method to obtain a {@link TransitionControllerBuilder} instance
     * @return {@link TransitionControllerBuilder}
     * @see #builder(Class)
     */
    @NonNull
    public static <K extends Enum<K>> TransitionControllerBuilder<K> builder() {
        return new TransitionControllerBuilder<>();
    }

    @NonNull
    public static <K extends Enum<K>> TransitionControllerBuilder<K> builder(@NonNull Class<K> type) {
        return new TransitionControllerBuilder<>();
    }


    @Nullable
    public abstract TransitionCallback forward(
            @Nullable Screen<K, ? extends Parcelable> from,
            @NonNull Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );

    /**
     * Execute `back` operation.
     *
     * Please note, that `from` and `to` screens are <em>reverted</em> here indicating REAL operation.
     * All bundled implementations are actually switching them when passing to {@link ScreenTransition}.
     *
     * @param from {@link Screen} that is being removed
     * @param to {@link Screen} that is GOING to be ACTIVE
     * @param endAction to invoke when transition finishes or {@link TransitionCallback} was
     *                  requested to be cancelled
     * @return {@link TransitionCallback} or null
     */
    @Nullable
    public abstract TransitionCallback back(
            @NonNull Screen<K, ? extends Parcelable> from,
            @Nullable Screen<K, ? extends Parcelable> to,
            @NonNull Runnable endAction
    );


    private static class Impl<K extends Enum<K>> extends TransitionController<K> {

        private final ScreenTransition<K> transition;

        public Impl(@NonNull ScreenTransition<K> transition) {
            this.transition = transition;
        }

        @Nullable
        @Override
        public TransitionCallback forward(@Nullable Screen<K, ? extends Parcelable> from, @NonNull Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return from == null
                    ? TransitionCallback.noOp(endAction)
                    : transition.apply(false, from, to, endAction);
        }

        @Nullable
        @Override
        public TransitionCallback back(@NonNull Screen<K, ? extends Parcelable> from, @Nullable Screen<K, ? extends Parcelable> to, @NonNull Runnable endAction) {
            return to == null
                    ? TransitionCallback.noOp(endAction)
                    : transition.apply(true, to, from, endAction);
        }
    }
}
