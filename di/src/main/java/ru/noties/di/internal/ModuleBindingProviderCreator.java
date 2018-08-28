package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Type;

import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;
import ru.noties.lazy.Lazy;

abstract class ModuleBindingProviderCreator {

    @NonNull
    abstract Di.Contributor create(@NonNull Key key, @NonNull ModuleBinding binding);

    @NonNull
    static ModuleBindingProviderCreator create(
            @NonNull InjectConstructorFinder injectConstructorFinder,
            @NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
        return new Impl(injectConstructorFinder, dependenciesDeclarationsCreator);
    }

    static class Impl extends ModuleBindingProviderCreator {

        private final InjectConstructorFinder injectConstructorFinder;
        private final DependenciesDeclarationsCreator dependenciesDeclarationsCreator;

        Impl(
                @NonNull InjectConstructorFinder injectConstructorFinder,
                @NonNull DependenciesDeclarationsCreator dependenciesDeclarationsCreator) {
            this.injectConstructorFinder = injectConstructorFinder;
            this.dependenciesDeclarationsCreator = dependenciesDeclarationsCreator;
        }

        @NonNull
        @Override
        Di.Contributor create(@NonNull Key key, @NonNull ModuleBinding binding) {

            // originType OR provider
            //  let's create an abstraction and make origin type through provider also
            //  if originType -> it can have also dependencies (must be validated... recursively)

            final Type originType = binding.originType();
            final Provider provider = binding.provider();

            final Type exact;
            if (provider == null) {
                exact = originType != null
                        ? originType
                        : binding.bindType();
            } else {
                exact = null;
            }

            // we actually could reuse implicit key validator logic here, (but it is operating on different abstractions
            //  we cannot use key here
            // if exact != null && !class -> throw cannot bind it

            Di.Contributor contributor;

            if (exact != null) {

                if (!(exact instanceof Class)) {
                    throw DiException.halt("Cannot instantiate an instance of: %s", exact);
                }

                final Class cl = (Class) exact;

                contributor = new FieldInjectionContributor(
                        injectConstructorFinder.find(cl),
                        dependenciesDeclarationsCreator.create(cl)
                );
            } else {
                contributor = new Di.Contributor() {
                    @NonNull
                    @Override
                    public Object contribute(@NonNull Di di) {
                        return provider.provide();
                    }
                };
            }

            if (binding.isProvider()) {
                contributor = new ProviderContributor(contributor);
            }

            if (binding.isLazy()) {
                contributor = new LazyContributor(contributor);
            }

            if (binding.isSingleton()) {
                contributor = new SingletonContributor(contributor);
            }

            return contributor;
        }

        private static class ProviderContributor implements Di.Contributor {

            private final Di.Contributor parent;

            ProviderContributor(@NonNull Di.Contributor parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object contribute(@NonNull final Di di) {
                return new Provider() {
                    @NonNull
                    @Override
                    public Object provide() {
                        return parent.contribute(di);
                    }
                };
            }
        }

        private static class LazyContributor implements Di.Contributor {

            private final Di.Contributor parent;

            LazyContributor(@NonNull Di.Contributor parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object contribute(@NonNull final Di di) {
                return Lazy.of(new Lazy.Provider<Object>() {
                    @NonNull
                    @Override
                    public Object provide() {
                        return parent.contribute(di);
                    }
                });
            }
        }

        private static class SingletonContributor implements Di.Contributor {

            private final Di.Contributor parent;
            private final Object lock = new Object();
            private Object value;

            SingletonContributor(@NonNull Di.Contributor parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object contribute(@NonNull Di di) {
                Object o = value;
                if (o == null) {
                    synchronized (lock) {
                        o = value;
                        if (o == null) {
                            o = value = parent.contribute(di);
                        }
                    }
                }
                return o;
            }
        }
    }
}
