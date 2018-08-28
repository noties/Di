package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.noties.di.ModuleBindingBuilder;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;

public abstract class ModuleHelper {

    @NonNull
    public abstract List<ModuleBinding> bindings();

    @NonNull
    public abstract <T> ModuleBindingBuilder.Typed<T> bind(@NonNull Class<T> type);

    @NonNull
    public abstract ModuleBindingBuilder.Raw bind(@NonNull Type type);


    @NonNull
    public static ModuleHelper create() {
        return new Impl();
    }

    static class Impl extends ModuleHelper {

        private final List<ModuleBinding> bindings = new ArrayList<>(3);

        @NonNull
        @Override
        public List<ModuleBinding> bindings() {
            return bindings;
        }

        @NonNull
        @Override
        public <T> ModuleBindingBuilder.Typed<T> bind(@NonNull Class<T> type) {
            final ModuleBindingImpl binding = new ModuleBindingImpl(type);
            bindings.add(binding);
            return new ModuleBindingBuilderTypedImpl<>(binding);
        }

        @NonNull
        @Override
        public ModuleBindingBuilder.Raw bind(@NonNull Type type) {
            final ModuleBindingImpl binding = new ModuleBindingImpl(type);
            bindings.add(binding);
            return new ModuleBindingBuilderImpl(binding);
        }

        private static class ModuleBindingBuilderImpl implements ModuleBindingBuilder, ModuleBindingBuilder.Raw, ModuleBindingBuilder.QualifiersOrModifiers {

            final ModuleBindingImpl binding;

            private ModuleBindingBuilderImpl(@NonNull ModuleBindingImpl binding) {
                this.binding = binding;
            }

            @NonNull
            @Override
            public Modifiers named(@NonNull String name) {
                binding.named = name;
                return this;
            }

            @NonNull
            @Override
            public Modifiers qualifier(@NonNull Class<? extends Annotation> qualifier) {
                binding.qualifier = qualifier;
                return this;
            }

            @NonNull
            @Override
            public Modifiers asLazy() {
                binding.isLazy = true;
                return this;
            }

            @NonNull
            @Override
            public Modifiers asSingleton() {
                binding.isSingleton = true;
                return this;
            }

            @NonNull
            @Override
            public Modifiers asProvider() {
                binding.isProvider = true;
                return this;
            }

            @NonNull
            @Override
            public QualifiersOrModifiers with(@NonNull Provider provider) {
                binding.provider = provider;
                return this;
            }

            @NonNull
            @Override
            public QualifiersOrModifiers as(@NonNull Type type) {
                binding.originType = type;
                return this;
            }
        }

        private static class ModuleBindingBuilderTypedImpl<T> extends ModuleBindingBuilderImpl implements ModuleBindingBuilder.Typed<T> {

            private ModuleBindingBuilderTypedImpl(@NonNull ModuleBindingImpl binding) {
                super(binding);
            }

            @NonNull
            @Override
            public QualifiersOrModifiers as(@NonNull Class<? extends T> type) {
                binding.originType = type;
                return this;
            }
        }
    }

    private static class ModuleBindingImpl implements ModuleBinding {

        private final Type bindType;

        private Type originType;
        private Provider provider;

        private String named;
        private Class<? extends Annotation> qualifier;

        private boolean isSingleton;
        private boolean isLazy;
        private boolean isProvider;

        ModuleBindingImpl(@NonNull Type bindType) {
            this.bindType = bindType;
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
    }
}
