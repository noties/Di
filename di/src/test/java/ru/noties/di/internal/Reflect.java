package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Reflect {

    private final Class cl;

    public Reflect(@NonNull Class cl) {
        this.cl = cl;
    }

    @NonNull
    public Constructor constructor(Class... parameters) {
        try {
            //noinspection unchecked
            return cl.getDeclaredConstructor(parameters);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @NonNull
    public Field field(@NonNull String name) {
        try {
            return cl.getDeclaredField(name);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
