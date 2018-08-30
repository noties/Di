package ru.noties.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.lang.reflect.Type;
import java.util.List;

import ru.noties.di.internal.ModuleHelper;

public abstract class Module {


    public abstract void configure();


    private final ModuleHelper moduleHelper;

    public Module() {
        this(ModuleHelper.create());
    }

    @VisibleForTesting
    Module(@NonNull ModuleHelper moduleHelper) {
        this.moduleHelper = moduleHelper;
    }

    public void init(@Nullable Di di) {
        moduleHelper.init(di);
    }

    @NonNull
    public List<ModuleBinding> bindings() {
        return moduleHelper.bindings();
    }

    @NonNull
    protected <T> ModuleBindingBuilder.Typed<T> bind(@NonNull Class<T> type) {
        return moduleHelper.bind(type);
    }

    @NonNull
    protected ModuleBindingBuilder.Raw bind(@NonNull Type type) {
        return moduleHelper.bind(type);
    }

    @NonNull
    protected <T> T require(@NonNull Class<T> type) {
        return moduleHelper.require(type);
    }

    @NonNull
    protected <T> T require(@NonNull Type type) {
        return moduleHelper.require(type);
    }

    @NonNull
    protected <T> T require(@NonNull Key key) {
        return moduleHelper.require(key);
    }
}
