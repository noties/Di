package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;
import ru.noties.di.reflect.ParameterizedTypeImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModuleHelperTest {

    private ModuleHelper.Impl impl;

    @Before
    public void before() {
        impl = new ModuleHelper.Impl();
    }

    @Test
    public void calling_require_on_root_di() {

        impl.init(null);

        final Runnable[] actions = {
                new Runnable() {
                    @Override
                    public void run() {
                        impl.require(String.class);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.require(new ParameterizedTypeImpl(List.class, null, String.class));
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        impl.require(Key.of(String.class));
                    }
                }
        };

        for (Runnable action : actions) {
            try {
                action.run();
                assertTrue(false);
            } catch (DiException e) {
                assertTrue(e.getMessage(), e.getMessage().contains("Calling #require when configuring module for " +
                        "the root Di instance"));
            }
        }
    }

    @Test
    public void typed_binding() {

        final Provider<String> stringProvider = new Provider<String>() {
            @NonNull
            @Override
            public String provide() {
                return "hey!";
            }
        };

        impl.bind(String.class);
        impl.bind(CharSequence.class).as(String.class);
        impl.bind(String.class).with(stringProvider);
        impl.bind(String.class).named("a-name");
        impl.bind(String.class).qualifier(NonNull.class);
        impl.bind(String.class).asSingleton();
        impl.bind(String.class).asLazy();
        impl.bind(String.class).asProvider();
        impl.bind(String.class).asLazy().asProvider().asSingleton();

        final List<ModuleBinding> expected = Arrays.asList(
                (ModuleBinding) new Binding(String.class),
                new Binding(CharSequence.class) {{
                    originType = String.class;
                }},
                new Binding(String.class) {{
                    provider = stringProvider;
                }},
                new Binding(String.class) {{
                    named = "a-name";
                }},
                new Binding(String.class) {{
                    qualifier = NonNull.class;
                }},
                new Binding(String.class) {{
                    isSingleton = true;
                }},
                new Binding(String.class) {{
                    isLazy = true;
                }},
                new Binding(String.class) {{
                    isProvider = true;
                }},
                new Binding(String.class) {{
                    isLazy = true;
                    isProvider = true;
                    isSingleton = true;
                }}
        );

        final List<ModuleBinding> actual = impl.bindings();
        assertEquals(expected, actual);
    }

    @Test
    public void raw_binding() {

        final Type type = (Type) String.class;

        final Provider<String> stringProvider = new Provider<String>() {
            @NonNull
            @Override
            public String provide() {
                return "hey!";
            }
        };

        impl.bind(type);
        impl.bind((Type) CharSequence.class).as(type);
        impl.bind(type).with(stringProvider);
        impl.bind(type).named("a-name");
        impl.bind(type).qualifier(NonNull.class);
        impl.bind(type).asSingleton();
        impl.bind(type).asLazy();
        impl.bind(type).asProvider();
        impl.bind(type).asLazy().asProvider().asSingleton();

        final List<ModuleBinding> expected = Arrays.asList(
                (ModuleBinding) new Binding(String.class),
                new Binding(CharSequence.class) {{
                    originType = String.class;
                }},
                new Binding(String.class) {{
                    provider = stringProvider;
                }},
                new Binding(String.class) {{
                    named = "a-name";
                }},
                new Binding(String.class) {{
                    qualifier = NonNull.class;
                }},
                new Binding(String.class) {{
                    isSingleton = true;
                }},
                new Binding(String.class) {{
                    isLazy = true;
                }},
                new Binding(String.class) {{
                    isProvider = true;
                }},
                new Binding(String.class) {{
                    isLazy = true;
                    isProvider = true;
                    isSingleton = true;
                }}
        );

        final List<ModuleBinding> actual = impl.bindings();
        assertEquals(expected, actual);
    }
}