package ru.noties.history;

import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

abstract class EntryIdGenerator {

    @NonNull
    static EntryIdGenerator create() {
        return new Impl();
    }


    abstract long next();


    private static class Impl extends EntryIdGenerator {

        private final AtomicInteger atomicInt = new AtomicInteger(0);

        @Override
        long next() {
            // UNIX time in seconds
            final int time = (int) (System.currentTimeMillis() / 1000);
            final int counter = atomicInt.incrementAndGet();
            return merge(time, counter);
        }

        // to extract merged values: left=(value >> 32), right=((int) value)
        private static long merge(int left, int right) {
            return ((long) left << 32) | (right & 0xffffffffL);
        }
    }
}
