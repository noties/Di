package ru.noties.di.internal;

import android.support.annotation.NonNull;

abstract class InternalDependencies {

    @NonNull
    abstract ExplicitBuilder explicitBuilder();

    @NonNull
    abstract ImplicitProviderCreator implicitProviderCreator();

    @NonNull
    abstract ServiceInjector serviceInjector();

    @NonNull
    static InternalDependencies create() {
        return new Impl();
    }

    static class Impl extends InternalDependencies {

        private final ExplicitBuilder explicitBuilder;
        private final ImplicitProviderCreator implicitProviderCreator;
        private final ServiceInjector serviceInjector;

        Impl() {

            final InjectConstructorFinder injectConstructorFinder = InjectConstructorFinder.create();

            final DependenciesDeclarationsCreator dependenciesDeclarationsCreator =
                    DependenciesDeclarationsCreator.create(KeyCreator.create());

            this.explicitBuilder = ExplicitBuilder.create(
                    ModuleMerger.create(ModuleBindingKeyCreator.create()),
                    ModuleBindingProviderCreator.create(injectConstructorFinder, dependenciesDeclarationsCreator));

            this.implicitProviderCreator = ImplicitProviderCreator.create(
                    ImplicitKeyValidator.create(),
                    injectConstructorFinder,
                    dependenciesDeclarationsCreator);

            this.serviceInjector = ServiceInjector.create(dependenciesDeclarationsCreator);
        }

        @NonNull
        @Override
        ExplicitBuilder explicitBuilder() {
            return explicitBuilder;
        }

        @NonNull
        @Override
        ImplicitProviderCreator implicitProviderCreator() {
            return implicitProviderCreator;
        }

        @NonNull
        @Override
        ServiceInjector serviceInjector() {
            return serviceInjector;
        }
    }
}
