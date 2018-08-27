package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

abstract class InjectConstructorFinder {

    @NonNull
    abstract Constructor find(@NonNull Class cl);

    @NonNull
    static InjectConstructorFinder create() {
        return new Impl();
    }

    static class Impl extends InjectConstructorFinder {

        // instance specific cache
        private final Map<Class, Constructor> cache = new HashMap<>(3);

        @NonNull
        @Override
        Constructor find(@NonNull Class cl) {

            // todo: check if this synchronization might become a bottleneck

            synchronized (cache) {
                Constructor constructor = cache.get(cl);
                if (constructor == null) {
                    constructor = obtain(cl);
                    cache.put(cl, constructor);
                }
                return constructor;
            }
        }

        @NonNull
        private Constructor obtain(@NonNull Class cl) {

            // it is required to annotate ONE constructor with @Inject (to make it explicit)
            //  but exactly one (object can have other constructors

            final Constructor[] constructors = cl.getDeclaredConstructors();

            Constructor injectedConstructor = null;

            for (Constructor constructor : constructors) {
                if (constructor.getAnnotation(Inject.class) != null) {

                    if (injectedConstructor != null) {
                        throw DiException.halt("Cannot create implicit dependency, type: " +
                                        "%s has multiple constructors annotated with @Inject annotation",
                                cl.getName());
                    }

                    final Type[] parameters = constructor.getParameterTypes();
                    if (parameters != null
                            && parameters.length > 0) {
                        throw DiException.halt("Cannot create implicit dependency, type: " +
                                        "%s has no empty constructor annotated with @Inject annotation",
                                cl.getName());
                    }

                    injectedConstructor = constructor;
                }
            }

            if (injectedConstructor == null) {
                throw DiException.halt("Cannot create implicit dependency, type: %s " +
                        "has no empty constructors annotated with @Inject annotation", cl.getName());
            }

            injectedConstructor.setAccessible(true);

            return injectedConstructor;
        }
    }
}
