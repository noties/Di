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
    abstract Di.Provider create(@NonNull Key key, @NonNull ModuleBinding binding);

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
        Di.Provider create(@NonNull Key key, @NonNull ModuleBinding binding) {

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

            Di.Provider diProvider;

            if (exact != null) {

                if (!(exact instanceof Class)) {
                    throw DiException.halt("Cannot instantiate an instance of: %s", exact);
                }

                final Class cl = (Class) exact;

                diProvider = new FieldInjectionProvider(
                        injectConstructorFinder.find(cl),
                        dependenciesDeclarationsCreator.create(cl)
                );
            } else {
                diProvider = new Di.Provider() {
                    @NonNull
                    @Override
                    public Object provide(@NonNull Di di) {
                        return provider.provide();
                    }
                };
            }

            // now, let's deal with singleton, lazy and provider
            // aha... we must change key here is lazy or provider

            // first provider

            // okay: lazy<provider> and singleton
            // singleton is the last one
            // then lazy
            // then provider

            // the thing is we might not need a special handling for provider?

            if (binding.isProvider()) {
                diProvider = new ProviderProvider(diProvider);
            }

            if (binding.isLazy()) {
                diProvider = new LazyProvider(diProvider);
            }

            if (binding.isSingleton()) {
                diProvider = new SingletonProvider(diProvider);
            }

            return diProvider;
        }

        private static class ProviderProvider implements Di.Provider {

            private final Di.Provider parent;

            ProviderProvider(@NonNull Di.Provider parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object provide(@NonNull final Di di) {
                return new Provider() {
                    @NonNull
                    @Override
                    public Object provide() {
                        return parent.provide(di);
                    }
                };
            }
        }

        private static class LazyProvider implements Di.Provider {

            private final Di.Provider parent;

            LazyProvider(@NonNull Di.Provider parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object provide(@NonNull final Di di) {
                return Lazy.of(new Lazy.Provider<Object>() {
                    @NonNull
                    @Override
                    public Object provide() {
                        return parent.provide(di);
                    }
                });
            }
        }

        private static class SingletonProvider implements Di.Provider {

            private final Di.Provider parent;
            private final Object lock = new Object();
            private Object value;

            SingletonProvider(@NonNull Di.Provider parent) {
                this.parent = parent;
            }

            @NonNull
            @Override
            public Object provide(@NonNull Di di) {
                Object o = value;
                if (o == null) {
                    synchronized (lock) {
                        o = value;
                        if (o == null) {
                            o = value = parent.provide(di);
                        }
                    }
                }
                return o;
            }
        }
    }
}
