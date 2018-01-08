package ru.noties.history.sample;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.util.Random;

abstract class Colors {

    @NonNull
    static Colors create() {
        return new Impl();
    }

    @ColorInt
    abstract int next();


    static class Impl extends Colors {

        private static final int[] COLORS = {
                0xffC62828,
                0xffAD1457,
                0xff6A1B9A,
                0xff4527A0,
                0xff283593,
                0xff1565C0,
                0xff0277BD,
                0xff00838F,
                0xff00695C,
                0xff2E7D32
        };

        private final Random random = new Random();

        @Override
        int next() {
            return COLORS[random.nextInt(COLORS.length)];
        }
    }
}
