package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.util.Collection;

import ru.noties.di.DiException;

abstract class CollectionUtils {

    @NonNull
    static <T> Collection<T> requireNoNulls(
            @NonNull String message,
            @NonNull Collection<T> collection) {
        for (T t : collection) {
            if (t == null) {
                throw DiException.halt(message);
            }
        }
        return collection;
    }

    private CollectionUtils() {
    }
}
