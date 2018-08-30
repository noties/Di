package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Named;

import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.internal.ImplicitKeyValidator.Impl;
import ru.noties.di.reflect.GenericArrayTypeImpl;
import ru.noties.di.reflect.ParameterizedTypeImpl;
import ru.noties.di.reflect.WildcardTypeImpl;

import static org.junit.Assert.assertTrue;

public class ImplicitKeyValidatorTest {

    private Impl impl;

    @Before
    public void before() {
        impl = new Impl();
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface MyQual {
    }

    @Test
    public void only_direct_class_allowed() {

        // for implicit dependencies only Class types are allowed

        impl.validate(Key.of(String.class));

        final Type[] types = {
                String[].class,
                new ParameterizedTypeImpl(List.class, null, ImplicitKeyValidator.class),
                new WildcardTypeImpl(new Type[]{Object.class}, new Type[]{Integer.class}),
                new GenericArrayTypeImpl(Boolean.class)
        };

        for (final Type type : types) {
            assertThrows(new Runnable() {
                @Override
                public void run() {
                    impl.validate(Key.of(type));
                }
            }, "Cannot create implicit dependency for the key");
        }

        final Key[] keys = {
                Key.of(String.class, "my-name"),
                Key.of(Short.class, MyQual.class),
                Key.of(Byte.class, Named.class)
        };

        for (final Key key : keys) {
            assertThrows(new Runnable() {
                @Override
                public void run() {
                    impl.validate(key);
                }
            }, "Cannot create implicit dependency with @Qualifier annotation");
        }
    }

    private void assertThrows(@NonNull Runnable runnable, @NonNull String messageContains) {
        try {
            runnable.run();
            assertTrue(false);
        } catch (DiException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(messageContains));
        }
    }
}