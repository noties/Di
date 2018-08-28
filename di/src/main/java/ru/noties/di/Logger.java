package ru.noties.di;

import android.support.annotation.NonNull;

public abstract class Logger {

    public abstract void log(@NonNull String tag, @NonNull String message);

    public abstract void log(@NonNull String tag, @NonNull String message, Object... args);

    public abstract boolean canLog();

    @NonNull
    public static Logger noOp() {
        return new NoOp();
    }

    static class NoOp extends Logger {
        @Override
        public void log(@NonNull String tag, @NonNull String message) {

        }

        @Override
        public void log(@NonNull String tag, @NonNull String message, Object... args) {

        }

        @Override
        public boolean canLog() {
            return false;
        }
    }
}
