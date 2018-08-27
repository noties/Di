package ru.noties.di.reflect;

import android.support.annotation.NonNull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Types {

    @NonNull
    public static Type canonicalize(Type type) {

        if (type instanceof DiType) {
            return type;
        }

        if (type instanceof Class) {
            final Class<?> c = (Class<?>) type;
            return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

        } else if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedTypeImpl(
                    p.getRawType(),
                    p.getOwnerType(),
                    p.getActualTypeArguments());

        } else if (type instanceof GenericArrayType) {
            final GenericArrayType g = (GenericArrayType) type;
            return new GenericArrayTypeImpl(g.getGenericComponentType());

        } else {
            return type;
        }
    }

    @NonNull
    static String typeToString(Type type) {
        return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
    }

    private Types() {
    }
}
