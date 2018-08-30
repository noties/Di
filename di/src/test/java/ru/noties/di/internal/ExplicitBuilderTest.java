package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;
import ru.noties.di.internal.ExplicitBuilder.Impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.noties.di.internal.TestUtils.with;

public class ExplicitBuilderTest {

    @Test
    public void no_modules() {
        // they must not be called
        final Impl impl = new Impl(null, null);
        final Map<Key, Di.Contributor> map = impl.build(null, Collections.<Module>emptyList());
        assertEquals(0, map.size());
    }

    @Test
    public void no_bindings() {

        final ModuleMerger moduleMerger = mock(ModuleMerger.class);
        when(moduleMerger.merge(any(Di.class), ArgumentMatchers.<Module>anyList())).thenReturn(Collections.<Key, ModuleBinding>emptyMap());
        final Impl impl = new Impl(moduleMerger, null);

        final Map<Key, Di.Contributor> map = impl.build(null, Arrays.asList(new Module() {
            @Override
            public void configure() {

            }
        }, new Module() {
            @Override
            public void configure() {

            }
        }));

        assertEquals(0, map.size());
        verify(moduleMerger, times(1)).merge(eq((Di) null), ArgumentMatchers.<Module>anyList());
    }

    @Test
    public void modules_and_bindings() {

        // real one
        final ModuleMerger moduleMerger = new ModuleMerger.Impl(new ModuleBindingKeyCreator.Impl());

        final ModuleBindingContributorCreator moduleBindingContributorCreator = mock(ModuleBindingContributorCreator.class);

        when(moduleBindingContributorCreator.create(any(ModuleBinding.class))).thenReturn(mock(Di.Contributor.class));

        final Impl impl = new Impl(moduleMerger, moduleBindingContributorCreator);

        final Module module1 = new Module() {
            @Override
            public void configure() {
                bind(String.class);
                bind(CharSequence.class).as(String.class);
            }
        };

        final Module module2 = new Module() {
            @Override
            public void configure() {
                bind(Module.class);
                bind(Di.Contributor.class).with(new Provider<Di.Contributor>() {
                    @NonNull
                    @Override
                    public Di.Contributor provide() {
                        return null;
                    }
                });
            }
        };

        final Map<Key, Di.Contributor> map = impl.build(null, Arrays.asList(module1, module2));

        assertEquals(4, map.size());

        with(Key.of(String.class), new TestUtils.Action<Key>() {
            @Override
            public void apply(@NonNull Key key) {
                assertNotNull(key.toString(), map.get(key));
            }
        });

        with(Key.of(CharSequence.class), new TestUtils.Action<Key>() {
            @Override
            public void apply(@NonNull Key key) {
                assertNotNull(key.toString(), map.get(key));
            }
        });

        with(Key.of(Module.class), new TestUtils.Action<Key>() {
            @Override
            public void apply(@NonNull Key key) {
                assertNotNull(key.toString(), map.get(key));
            }
        });

        with(Key.of(Di.Contributor.class), new TestUtils.Action<Key>() {
            @Override
            public void apply(@NonNull Key key) {
                assertNotNull(key.toString(), map.get(key));
            }
        });

        // also: returned map must be mutable
        map.clear();
    }
}