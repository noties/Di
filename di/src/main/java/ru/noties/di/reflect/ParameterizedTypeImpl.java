package ru.noties.di.reflect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import static ru.noties.di.reflect.Types.canonicalize;
import static ru.noties.di.reflect.Types.typeToString;

public class ParameterizedTypeImpl implements ParameterizedType, DiType {

    private final Type rawType;
    private final Type ownerType;
    private final Type[] actualTypeArguments;

    public ParameterizedTypeImpl(@NonNull Type rawType, @Nullable Type ownerType, @NonNull Type... actualTypeArguments) {
        this.rawType = canonicalize(rawType);
        this.ownerType = ownerType != null
                ? canonicalize(ownerType)
                : null;

        for (int i = 0, length = actualTypeArguments.length; i < length; i++) {
            actualTypeArguments[i] = canonicalize(actualTypeArguments[i]);
        }
        this.actualTypeArguments = actualTypeArguments;
    }

    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @NonNull
    @Override
    public Type getRawType() {
        return rawType;
    }

    @Nullable
    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ParameterizedType)) {
            return false;
        }

        final ParameterizedType parameterizedType = (ParameterizedType) o;
        return ObjectUtils.equals(this.rawType, parameterizedType.getRawType())
                && ObjectUtils.equals(this.ownerType, parameterizedType.getOwnerType())
                && Arrays.equals(this.actualTypeArguments, parameterizedType.getActualTypeArguments());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.actualTypeArguments)
                ^ ObjectUtils.hashCode(this.rawType)
                ^ ObjectUtils.hashCode(this.ownerType);
    }

    @Override
    public String toString() {
        int length = actualTypeArguments.length;
        if (length == 0) {
            return typeToString(rawType);
        }

        final StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
        stringBuilder.append(typeToString(rawType)).append("<").append(typeToString(actualTypeArguments[0]));
        for (int i = 1; i < length; i++) {
            stringBuilder.append(", ").append(typeToString(actualTypeArguments[i]));
        }
        return stringBuilder.append(">").toString();
    }
}
