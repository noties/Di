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

abstract class ModuleMerger {

    @NonNull
    abstract Map<Key, ModuleBinding> merge(@Nullable Di di, @NonNull Collection<Module> modules);

    @NonNull
    static ModuleMerger create(@NonNull ModuleBindingKeyCreator moduleBindingKeyCreator) {
        return new Impl(moduleBindingKeyCreator);
    }

    static class Impl extends ModuleMerger {

        private final ModuleBindingKeyCreator keyCreator;

        Impl(@NonNull ModuleBindingKeyCreator keyCreator) {
            this.keyCreator = keyCreator;
        }

        @NonNull
        @Override
        Map<Key, ModuleBinding> merge(@Nullable Di di, @NonNull Collection<Module> modules) {

            final Map<Key, ModuleBinding> map = new HashMap<>(3);

            Key key;

            for (Module module : modules) {
                module.init(di);
                module.configure();
                for (ModuleBinding binding : module.bindings()) {
                    key = keyCreator.create(binding);
                    if (map.put(key, binding) != null) {
                        throw DiException.halt("Multiple modules bind same " +
                                "dependency: %s, %s", key, binding);
                    }
                }
            }

            return MapUtils.freeze(map);
        }
    }
}
