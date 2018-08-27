package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.ModuleBinding;

abstract class ExplicitBuilder {

    @NonNull
    abstract Map<Key, Di.Provider> build(@NonNull Collection<Module> modules);

    @NonNull
    static ExplicitBuilder create(
            @NonNull ModuleMerger moduleMerger,
            @NonNull ModuleBindingProviderCreator moduleBindingProviderCreator) {
        return new Impl(moduleMerger, moduleBindingProviderCreator);
    }

    static class Impl extends ExplicitBuilder {

        private final ModuleMerger moduleMerger;
        private final ModuleBindingProviderCreator moduleBindingProviderCreator;

        Impl(
                @NonNull ModuleMerger moduleMerger,
                @NonNull ModuleBindingProviderCreator moduleBindingProviderCreator) {
            this.moduleMerger = moduleMerger;
            this.moduleBindingProviderCreator = moduleBindingProviderCreator;
        }

        @NonNull
        @Override
        Map<Key, Di.Provider> build(@NonNull Collection<Module> modules) {

            // first merge all bindings from all supplied modules into a list
            // then walk the collection and create providers
            final Map<Key, Di.Provider> map = new HashMap<>(3);

            if (modules.size() > 0) {
                final Map<Key, ModuleBinding> bindings = moduleMerger.merge(modules);
                for (Map.Entry<Key, ModuleBinding> entry : bindings.entrySet()) {
                    map.put(entry.getKey(), moduleBindingProviderCreator.create(entry.getKey(), entry.getValue()));
                }
            }

            return MapUtils.freeze(map);
        }
    }
}
