package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import ru.noties.di.Key;

abstract class KeyCreator {

    @Nullable
    abstract Key createKey(@NonNull Field field);

    @NonNull
    static KeyCreator create() {
        return new Impl();
    }

    static class Impl extends KeyCreator {

        @Nullable
        @Override
        Key createKey(@NonNull Field field) {

            if (field.getAnnotation(Inject.class) == null) {
                return null;
            }

            final Type genericType = field.getGenericType();
            final Annotation[] annotations = field.getDeclaredAnnotations();

            if (annotations != null
                    && annotations.length > 0) {

                Annotation qualifier = null;

                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getAnnotation(Qualifier.class) != null) {

                        if (qualifier != null) {
                            // error, another one is found
                            throw DiException.halt("%s#%s, multiple @Qualifier " +
                                    "annotations found", field.getDeclaringClass().getName(), field.getName());
                        }

                        qualifier = annotation;
                    }
                }

                if (qualifier != null) {

                    final Class<? extends Annotation> type = qualifier.annotationType();

                    // here we can directly check if it's named
                    if (Named.class.equals(type)) {
                        return Key.of(genericType, ((Named) qualifier).value());
                    } else {
                        return Key.of(genericType, type);
                    }
                }
            }

            // if we are here -> Direct
            return Key.of(genericType);
        }
    }
}
