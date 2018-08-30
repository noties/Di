package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;

class Binding implements ModuleBinding {

    final Type bindType;

    Type originType;
    Provider provider;
    String named;
    Class<? extends Annotation> qualifier;
    boolean isSingleton;
    boolean isLazy;
    boolean isProvider;

    Binding(@NonNull Type bindType) {
        this.bindType = bindType;
    }

    Binding(@NonNull ModuleBinding binding) {
        this.bindType = binding.bindType();
        this.originType = binding.originType();
        this.provider = binding.provider();
        this.named = binding.named();
        this.qualifier = binding.qualifier();
        this.isSingleton = binding.isSingleton();
        this.isLazy = binding.isLazy();
        this.isProvider = binding.isProvider();
    }

    @NonNull
    @Override
    public Type bindType() {
        return bindType;
    }

    @Nullable
    @Override
    public Type originType() {
        return originType;
    }

    @Nullable
    @Override
    public Provider provider() {
        return provider;
    }

    @Nullable
    @Override
    public String named() {
        return named;
    }

    @Nullable
    @Override
    public Class<? extends Annotation> qualifier() {
        return qualifier;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Override
    public boolean isLazy() {
        return isLazy;
    }

    @Override
    public boolean isProvider() {
        return isProvider;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ModuleBinding)) {
            return false;
        }

        ModuleBinding binding = (ModuleBinding) o;

        if (isSingleton != binding.isSingleton()) return false;
        if (isLazy != binding.isLazy()) return false;
        if (isProvider != binding.isProvider()) return false;
        if (bindType != null ? !bindType.equals(binding.bindType()) : binding.bindType() != null)
            return false;
        if (originType != null ? !originType.equals(binding.originType()) : binding.originType() != null)
            return false;
        if (provider != null ? !provider.equals(binding.provider()) : binding.provider() != null)
            return false;
        if (named != null ? !named.equals(binding.named()) : binding.named() != null) return false;
        return qualifier != null ? qualifier.equals(binding.qualifier()) : binding.qualifier() == null;
    }

    @Override
    public int hashCode() {
        int result = bindType != null ? bindType.hashCode() : 0;
        result = 31 * result + (originType != null ? originType.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (named != null ? named.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (isSingleton ? 1 : 0);
        result = 31 * result + (isLazy ? 1 : 0);
        result = 31 * result + (isProvider ? 1 : 0);
        return result;
    }
}
