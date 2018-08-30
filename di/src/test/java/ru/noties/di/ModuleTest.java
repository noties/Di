package ru.noties.di;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import ru.noties.di.internal.ModuleHelper;
import ru.noties.di.reflect.ParameterizedTypeImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ModuleTest {

    @Test
    public void all_methods_redirect_to_helper() {

        final ModuleHelper helper = mock(ModuleHelper.class);
        final Module module = new Module(helper) {
            @Override
            public void configure() {
                // no-op
            }
        };

        final Type type = new ParameterizedTypeImpl(List.class, null, CharSequence.class);

        module.init(null);
        module.bind(String.class);
        module.bind(type);
        module.require(String.class);
        module.require(type);
        module.require(Key.of(String.class));
        module.bindings();

        verify(helper, times(1)).init(null);
        verify(helper, times(1)).bind(String.class);
        verify(helper, times(1)).bind(type);
        verify(helper, times(1)).require(String.class);
        verify(helper, times(1)).require(type);
        verify(helper, times(1)).require(Key.of(String.class));
        verify(helper, times(1)).bindings();
    }
}