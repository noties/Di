package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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

            validateConcreteClass(cl);

            // it is required to annotate ONE constructor with @Inject (to make it explicit)
            //  but exactly one (object can have other constructors

            final Constructor[] constructors = cl.getDeclaredConstructors();

            Constructor injectedConstructor = null;

            for (Constructor constructor : constructors) {
                if (constructor.getAnnotation(Inject.class) != null) {

                    if (injectedConstructor != null) {
                        // Multiple constructors with @Inject annotation, type: %s
                        throw DiException.halt("Multiple constructors with @Inject annotation, " +
                                "type: %s", cl.getName());
                    }

                    final Type[] parameters = constructor.getParameterTypes();
                    if (parameters != null
                            && parameters.length > 0) {
                        // Constructor with @Inject annotation is not empty (has parameters)
                        throw DiException.halt("Constructor with @Inject annotation is " +
                                "not empty (has parameters), type: %s", cl.getName());
                    }

                    injectedConstructor = constructor;
                }
            }

            if (injectedConstructor == null) {
                // No empty constructor with @Inject annotation found, type: %s
                throw DiException.halt("No empty constructor with @Inject annotation found, " +
                        "type: %s", cl.getName());
            }

            injectedConstructor.setAccessible(true);

            return injectedConstructor;
        }

        private static void validateConcreteClass(@NonNull Class cl) {

            //  * !isAnnotation
            //  * !isAnonymousClass
            //  * !isArray
            //  * !isEnum
            //  * !isInterface
            //  * !isLocalClass
            //  * !Modifiers.isAbstract()
            //  * isMemberClass ? Modifiers.isStatic() : false
            //  * !isPrimitive
            //  * !isSynthetic

            final String reason;

            if (cl.isAnnotation()) {
                reason = "annotation";
            } else if (cl.isAnonymousClass()) {
                reason = "anonymous class";
            } else if (cl.isArray()) {
                reason = "array";
            } else if (cl.isEnum()) {
                reason = "enum";
            } else if (cl.isInterface()) {
                reason = "interface";
            } else if (cl.isLocalClass()) {
                reason = "local class";
            } else if (cl.isPrimitive()) {
                // move primitive before abstract check as int.class for example is abstract
                reason = "primitive";
            } else if (Modifier.isAbstract(cl.getModifiers())) {
                reason = "abstract class";
            } else if (cl.isMemberClass() && !Modifier.isStatic(cl.getModifiers())) {
                reason = "non-static member class";
            } else if (cl.isSynthetic()) {
                reason = "synthetic class";
            } else {
                reason = null;
            }

            if (reason != null) {
                throw DiException.halt("Provided type cannot be instantiated, reason: %s, " +
                        "cl: %s(%s)", reason, cl.getName(), cl);
            }
        }
    }
}
