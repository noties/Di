package ru.noties.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import ru.noties.di.internal.DiException;
import ru.noties.di.internal.ModuleHelper;

public abstract class Module {

    public abstract void configure();


    private final ModuleHelper moduleHelper = ModuleHelper.create();

    private Di di;

    public void init(@Nullable Di di) {
        this.di = di;
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
        return require(Key.of(type));
    }

    @NonNull
    protected <T> T require(@NonNull Type type) {
        return require(Key.of(type));
    }

    @NonNull
    protected <T> T require(@NonNull Key key) {
        if (di != null) {
            return di.get(key);
        }
        throw DiException.halt("Calling #require on the root Di instance");
    }
}
