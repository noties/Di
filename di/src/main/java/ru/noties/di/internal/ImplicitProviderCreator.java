package ru.noties.di.internal;

import android.support.annotation.NonNull;

import ru.noties.di.Di;
import ru.noties.di.Key;

abstract class ImplicitProviderCreator {

    @NonNull
    abstract Di.Provider create(@NonNull Key key);

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
        Di.Provider create(@NonNull Key key) {

            // todo: cache?

            implicitKeyValidator.validate(key);

            final Class cl = (Class) key.type();

            return new FieldInjectionProvider(
                    injectConstructorFinder.find(cl),
                    dependenciesDeclarationsCreator.create(cl)
            );
        }
    }
}
