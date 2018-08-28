package ru.noties.di.reflect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import static ru.noties.di.reflect.Types.canonicalize;
import static ru.noties.di.reflect.Types.typeToString;

public class WildcardTypeImpl implements WildcardType, DiType {

    private final Type upperBound;
    private final Type lowerBound;

    private final Type[] upperBounds;
    private final Type[] lowerBounds;

    public WildcardTypeImpl(@NonNull Type[] upperBounds, @NonNull Type[] lowerBounds) {

        checkArgument(lowerBounds.length <= 1);
        checkArgument(upperBounds.length == 1);

        if (lowerBounds.length == 1) {
            checkNotNull(lowerBounds[0]);
            checkNotPrimitive(lowerBounds[0]);
            checkArgument(upperBounds[0] == Object.class);
            this.lowerBound = canonicalize(lowerBounds[0]);
            this.upperBound = Object.class;

        } else {
            checkNotNull(upperBounds[0]);
            checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = canonicalize(upperBounds[0]);
        }

        this.upperBounds = new Type[]{upperBound};
        this.lowerBounds = lowerBound != null
                ? new Type[]{lowerBound}
                : new Type[0];
    }

    @NonNull
    @Override
    public Type[] getUpperBounds() {
        return upperBounds;
    }

    @NonNull
    @Override
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WildcardType)) {
            return false;
        }

        WildcardType wildcardType = (WildcardType) other;
        return Arrays.equals(upperBounds, wildcardType.getUpperBounds())
                && Arrays.equals(lowerBounds, wildcardType.getLowerBounds());
    }

    @Override
    public int hashCode() {
        // this equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
        return (lowerBound != null ? 31 + lowerBound.hashCode() : 1)
                ^ (31 + upperBound.hashCode());
    }

    @Override
    public String toString() {
        if (lowerBound != null) {
            return "? super " + typeToString(lowerBound);
        } else if (upperBound == Object.class) {
            return "?";
        } else {
            return "? extends " + typeToString(upperBound);
        }
    }

    private static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    private static void checkNotNull(@Nullable Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    private static void checkNotPrimitive(Type type) {
        checkArgument(!(type instanceof Class<?>) || !((Class<?>) type).isPrimitive());
    }
}