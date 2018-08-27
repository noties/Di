package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Type;

import ru.noties.di.Key;

abstract class ImplicitKeyValidator {

    abstract void validate(@NonNull Key key);

    @NonNull
    static ImplicitKeyValidator create() {
        return new Impl();
    }

    static class Impl extends ImplicitKeyValidator {

        @Override
        void validate(@NonNull Key key) {

            if (!(key instanceof Key.Direct)) {
                throw DiException.halt("Cannot create implicit dependency with " +
                        "@Qualifier annotation, key: %s", key);
            }

            final Type type = key.type();
            if (!(type instanceof Class)) {
                throw DiException.halt("Cannot create implicit dependency for the " +
                        "key: %s", key);
            }
        }
    }
}
