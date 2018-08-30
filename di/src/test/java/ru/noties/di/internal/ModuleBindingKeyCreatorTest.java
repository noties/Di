package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;

import ru.noties.di.Key;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;
import ru.noties.di.internal.ModuleBindingKeyCreator.Impl;
import ru.noties.di.reflect.ParameterizedTypeImpl;
import ru.noties.lazy.Lazy;

import static org.junit.Assert.assertEquals;

public class ModuleBindingKeyCreatorTest {

    private Impl impl;

    @Before
    public void before() {
        impl = new Impl();
    }

    @Test
    public void direct() {
        assertKey(
                Key.of(String.class),
                new BindingImpl(String.class)
        );
    }

    @Test
    public void named() {
        assertKey(
                Key.of(Boolean.TYPE, "my-name-is-the-bool"),
                new BindingImpl(Boolean.TYPE) {{
                    named = "my-name-is-the-bool";
                }}
        );
    }

    @Test
    public void qualified() {
        assertKey(
                Key.of(Byte.class, NonNull.class),
                new BindingImpl(Byte.class) {{
                    qualifier = NonNull.class;
                }}
        );
    }

    @Test
    public void lazy_lazy() {
        // if key is originall lazy -> Lazy<Lazy<>>
        final Key key = Key.of(new ParameterizedTypeImpl(Lazy.class, null, CharSequence.class));
        assertKeyLazy(key, new BindingImpl(key.type()));
    }

    @Test
    public void provider_provider() {
        final Key key = Key.of(new ParameterizedTypeImpl(Provider.class, null, Runnable.class));
        assertKeyProvider(key, new BindingImpl(key.type()));
    }

    @Test
    public void provider_provider_lazy() {
        final Key key = Key.of(
                new ParameterizedTypeImpl(Provider.class, null, new ParameterizedTypeImpl(Lazy.class, null, Class.class))
        );
        assertKeyProvider(key, new BindingImpl(key.type()));
    }

    private void assertKey(@NonNull Key key, @NonNull ModuleBinding binding) {
        assertKeySimple(key, binding);
        assertKeyLazy(key, binding);
        assertKeyProvider(key, binding);
        assertKeyLazyProvider(key, binding);
    }

    private void assertKeySimple(@NonNull Key key, @NonNull ModuleBinding binding) {
        assertEquals(key.toString(), key, impl.create(binding));
    }

    private void assertKeyLazy(@NonNull Key key, @NonNull ModuleBinding binding) {
        final Type lazyType = new ParameterizedTypeImpl(Lazy.class, null, key.type());
        assertKeySimple(
                newKeyType(key, lazyType),
                new BindingImpl(binding) {{
                    isLazy = true;
                }}
        );
    }

    private void assertKeyProvider(@NonNull Key key, @NonNull ModuleBinding binding) {
        final Type providerType = new ParameterizedTypeImpl(Provider.class, null, key.type());
        assertKeySimple(
                newKeyType(key, providerType),
                new BindingImpl(binding) {{
                    isProvider = true;
                }}
        );
    }

    private void assertKeyLazyProvider(@NonNull Key key, @NonNull ModuleBinding binding) {
        final Type lazyProviderType = new ParameterizedTypeImpl(
                Lazy.class,
                null,
                new ParameterizedTypeImpl(Provider.class, null, key.type())
        );
        assertKeySimple(
                newKeyType(key, lazyProviderType),
                new BindingImpl(binding) {{
                    isLazy = true;
                    isProvider = true;
                }}
        );
    }

    @NonNull
    private static Key newKeyType(@NonNull Key origin, @NonNull Type type) {
        final Key key;
        if (origin instanceof Key.Named) {
            key = Key.of(
                    type,
                    ((Key.Named) origin).name()
            );
        } else if (origin instanceof Key.Qualified) {
            key = Key.of(
                    type,
                    ((Key.Qualified) origin).qualifier()
            );
        } else {
            key = Key.of(type);
        }
        return key;
    }

    private static class BindingImpl extends Binding {

        BindingImpl(@NonNull Type bindType) {
            super(bindType);
        }

        BindingImpl(@NonNull ModuleBinding binding) {
            super(binding.bindType());
//            this.originType = binding.originType();
//            this.provider = binding.provider();
            this.named = binding.named();
            this.qualifier = binding.qualifier();
//            this.isSingleton = binding.isSingleton();
            this.isLazy = binding.isLazy();
            this.isProvider = binding.isProvider();
        }

        @Nullable
        @Override
        public Type originType() {
            throw new RuntimeException("Unexpected method call");
        }

        @Nullable
        @Override
        public Provider provider() {
            throw new RuntimeException("Unexpected method call");
        }

        @Override
        public boolean isSingleton() {
            throw new RuntimeException("Unexpected method call");
        }
    }

}