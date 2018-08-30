package ru.noties.di.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.ModuleBinding;
import ru.noties.di.internal.ModuleMerger.Impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ModuleMergerTest {

    private Impl impl;

    @Before
    public void before() {
        impl = new Impl(ModuleBindingKeyCreator.create());
    }

    @Test
    public void all_modules_init_and_configure() {

        final Collection<Module> modules = Arrays.asList(
                mock(Module.class),
                mock(Module.class)
        );

        impl.merge(null, modules);

        for (Module module : modules) {
            verify(module, times(1)).init(null);
            verify(module, times(1)).configure();
            verify(module, times(1)).bindings();
        }
    }

    @Test
    public void single_module() {

        final Module module = new Module() {
            @Override
            public void configure() {

                bind(CharSequence.class).as(String.class);

                bind(Integer.class);
            }
        };

        final Map<Key, ModuleBinding> map = impl.merge(null, Collections.singletonList(module));
        assertEquals(2, map.size());

        assertNotNull(map.get(Key.of(CharSequence.class)));
        assertNotNull(map.get(Key.of(Integer.class)));
    }

    @Test
    public void multiple_modules() {

        final Module module1 = new Module() {
            @Override
            public void configure() {
                bind(Byte.TYPE);
            }
        };

        final Module module2 = new Module() {
            @Override
            public void configure() {
                bind(Short.class);
            }
        };

        final Map<Key, ModuleBinding> map = impl.merge(null, Arrays.asList(module1, module2));
        assertEquals(2, map.size());
        assertNotNull(map.get(Key.of(Byte.TYPE)));
        assertNotNull(map.get(Key.of(Short.class)));
    }

    @Test
    public void duplicate_keys_in_modules() {
        try {
            impl.merge(null, Arrays.asList(
                    new Module() {
                        @Override
                        public void configure() {
                            bind(CharSequence.class).as(String.class);
                        }
                    },
                    new Module() {
                        @Override
                        public void configure() {
                            bind(CharSequence.class).as(String.class);
                        }
                    }
            ));
        } catch (DiException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Multiple modules bind same dependency:"));
        }
    }
}