package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;

abstract class ServiceInjector {

    abstract void inject(@NonNull Di di, @NonNull Di.Service service);

    @NonNull
    static ServiceInjector create(@NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
        return new Impl(dependenciesDeclarationsCreator);
    }

    static class Impl extends ServiceInjector {

        private final DependenciesDeclarationsCreator dependenciesDeclarationsCreator;

        Impl(@NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
            this.dependenciesDeclarationsCreator = dependenciesDeclarationsCreator;
        }

        @Override
        void inject(@NonNull Di di, @NonNull Di.Service service) {
            final Map<Field, Key> map = dependenciesDeclarationsCreator.create(service.getClass());
            for (Map.Entry<Field, Key> entry : map.entrySet()) {
                final Object o = di.get(entry.getValue());
                try {
                    entry.getKey().set(service, o);
                } catch (IllegalAccessException e) {
                    throw DiException.halt("%s: cannot inject %s#%s with " +
                            "value: %s", di.path(), service.getClass().getName(), entry.getKey().getName(), o);
                }
            }
        }
    }
}
