package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;

public abstract class ReflectUtils {

    @NonNull
    public static Field field(@NonNull Class<?> type, @NonNull String name) {
        try {
            return type.getDeclaredField(name);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private ReflectUtils() {
    }
}
