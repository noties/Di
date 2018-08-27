package ru.noties.di;

import android.support.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.List;

import ru.noties.di.internal.ModuleHelper;

public abstract class Module {

    public abstract void configure();

    private final ModuleHelper moduleHelper = ModuleHelper.create();

    @NonNull
    public List<ModuleBinding> bindings() {
        return moduleHelper.bindings();
    }

    @NonNull
    protected <T> Binding.Typed<T> bind(@NonNull Class<T> type) {
        return moduleHelper.bind(type);
    }

    @NonNull
    protected Binding.Raw bind(@NonNull Type type) {
        return moduleHelper.bind(type);
    }
}
