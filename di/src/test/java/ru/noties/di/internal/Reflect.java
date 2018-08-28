package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

public class Reflect {

    private final Class cl;

    public Reflect(@NonNull Class cl) {
        this.cl = cl;
    }

    @NonNull
    public Field field(@NonNull String name) {
        try {
            return cl.getDeclaredField(name);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Nullable
    public Field fieldOptional(@NonNull String name) {
        try {
            return cl.getDeclaredField(name);
        } catch (Throwable t) {
            return null;
        }
    }
}
