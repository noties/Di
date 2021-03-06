package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.ModuleBinding;

abstract class ExplicitBuilder {

    @NonNull
    abstract Map<Key, Di.Contributor> build(@Nullable Di parent, @NonNull Collection<Module> modules);

    @NonNull
    static ExplicitBuilder create(
            @NonNull ModuleMerger moduleMerger,
            @NonNull ModuleBindingContributorCreator moduleBindingContributorCreator) {
        return new Impl(moduleMerger, moduleBindingContributorCreator);
    }

    static class Impl extends ExplicitBuilder {

        private final ModuleMerger moduleMerger;
        private final ModuleBindingContributorCreator moduleBindingContributorCreator;

        Impl(
                @NonNull ModuleMerger moduleMerger,
                @NonNull ModuleBindingContributorCreator moduleBindingContributorCreator) {
            this.moduleMerger = moduleMerger;
            this.moduleBindingContributorCreator = moduleBindingContributorCreator;
        }

        @NonNull
        @Override
        Map<Key, Di.Contributor> build(@Nullable Di parent, @NonNull Collection<Module> modules) {

            // first merge all bindings from all supplied modules into a list
            // then walk the collection and create providers
            final Map<Key, Di.Contributor> map = new HashMap<>(3);

            if (modules.size() > 0) {
                final Map<Key, ModuleBinding> bindings = moduleMerger.merge(parent, modules);
                for (Map.Entry<Key, ModuleBinding> entry : bindings.entrySet()) {
                    map.put(entry.getKey(), moduleBindingContributorCreator.create(entry.getValue()));
                }
            }

            // cannot freeze it as we want to clear it after di is closed
            return map;
        }
    }
}
