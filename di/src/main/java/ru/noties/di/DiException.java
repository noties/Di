package ru.noties.di;

import android.support.annotation.NonNull;

import java.util.Locale;

public class DiException extends RuntimeException {

    // let's include version info with all runtime exceptions
    private static final String PREFIX = "Di[v" + BuildConfig.VERSION_NAME + "] ";

    @NonNull
    public static DiException halt(@NonNull String message, Object... args) {
        return new DiException(message(message, args));
    }

    @NonNull
    public static DiException halt(@NonNull Throwable cause, @NonNull String message, Object... args) {
        return new DiException(message(message, args), cause);
    }

    @NonNull
    private static String message(@NonNull String message, Object... args) {
        return PREFIX + String.format(Locale.US, message, args);
    }

    private DiException(@NonNull String message) {
        super(message);
    }

    private DiException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
