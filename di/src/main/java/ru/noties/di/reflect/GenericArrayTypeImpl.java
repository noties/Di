package ru.noties.di.reflect;

import android.support.annotation.NonNull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import static ru.noties.di.reflect.Types.canonicalize;
import static ru.noties.di.reflect.Types.typeToString;

public class GenericArrayTypeImpl implements GenericArrayType, DiType {

    private final Type componentType;

    public GenericArrayTypeImpl(@NonNull Type componentType) {
        this.componentType = canonicalize(componentType);
    }

    @NonNull
    public Type getGenericComponentType() {
        return componentType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericArrayType)) {
            return false;
        }

        GenericArrayType genericArrayType = (GenericArrayType) o;
        return ObjectUtils.equals(componentType, genericArrayType.getGenericComponentType());
    }

    @Override
    public int hashCode() {
        return componentType.hashCode();
    }

    @Override
    public String toString() {
        return typeToString(componentType) + "[]";
    }
}
