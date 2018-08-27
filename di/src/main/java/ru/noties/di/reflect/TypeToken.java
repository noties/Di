package ru.noties.di.reflect;

import android.support.annotation.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static ru.noties.di.reflect.Types.canonicalize;

public abstract class TypeToken<T> {

    @NonNull
    public Type getType() {
        final Type type = getClass().getGenericSuperclass();
        return canonicalize(((ParameterizedType) type).getActualTypeArguments()[0]);
    }
}
