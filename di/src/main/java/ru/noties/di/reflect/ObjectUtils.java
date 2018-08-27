package ru.noties.di.reflect;

abstract class ObjectUtils {

    static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    private ObjectUtils() {
    }
}
