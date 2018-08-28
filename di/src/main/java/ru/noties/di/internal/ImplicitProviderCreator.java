package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;

abstract class ImplicitProviderCreator {

    @NonNull
    abstract Di.Contributor create(@NonNull Key key);

    @NonNull
    static ImplicitProviderCreator create(
            @NonNull ImplicitKeyValidator implicitKeyValidator,
            @NonNull InjectConstructorFinder injectConstructorFinder,
            @NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
        return new Impl(
                implicitKeyValidator,
                injectConstructorFinder,
                dependenciesDeclarationsCreator
        );
    }

    static class Impl extends ImplicitProviderCreator {

        private final ImplicitKeyValidator implicitKeyValidator;
        private final InjectConstructorFinder injectConstructorFinder;
        private final DependenciesDeclarationsCreator dependenciesDeclarationsCreator;
        private final Map<Key, Di.Contributor> cache = new HashMap<>(3);

        Impl(
                @NonNull ImplicitKeyValidator implicitKeyValidator,
                @NonNull InjectConstructorFinder injectConstructorFinder,
                @NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
            this.implicitKeyValidator = implicitKeyValidator;
            this.injectConstructorFinder = injectConstructorFinder;
            this.dependenciesDeclarationsCreator = dependenciesDeclarationsCreator;
        }

        @NonNull
        @Override
        Di.Contributor create(@NonNull Key key) {
            synchronized (cache) {
                Di.Contributor contributor = cache.get(key);
                if (contributor == null) {
                    contributor = obtain(key);
                    cache.put(key, contributor);
                }
                return contributor;
            }
        }

        @NonNull
        private Di.Contributor obtain(@NonNull Key key) {

            implicitKeyValidator.validate(key);

            final Class cl = (Class) key.type();

            return new FieldInjectionContributor(
                    injectConstructorFinder.find(cl),
                    dependenciesDeclarationsCreator.create(cl)
            );
        }
    }
}
