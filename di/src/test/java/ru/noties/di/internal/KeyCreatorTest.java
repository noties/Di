package ru.noties.di.internal;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import ru.noties.di.Key;
import ru.noties.di.reflect.TypeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KeyCreatorTest {

    private KeyCreator.Impl impl;
    private Reflect reflect;

    @Before
    public void before() {
        impl = new KeyCreator.Impl();
        reflect = new Reflect(T.class);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MyQual {
    }

    private static class T {

        private Boolean b;

        @Inject
        private String s;

        @Named("hello-i")
        @MyQual
        @Inject
        private int i;

        @Inject
        private byte flag;

        @Inject
        @Named("a-long-list")
        private List<Long> named;

        @MyQual
        @Inject
        private T t;
    }

    @Test
    public void no_inject_returns_null() {
        assertNull(impl.createKey(reflect.field("b")));
    }

    @Test
    public void simple_inject() {
        assertNotNull(impl.createKey(reflect.field("s")));
    }

    @Test
    public void multiple_qualifier_annotations_throw() {
        try {
            impl.createKey(reflect.field("i"));
            assertTrue(false);
        } catch (DiException e) {
            assertTrue(e.getMessage().contains("multiple @Qualifier " +
                    "annotations found"));
        }
    }

    @Test
    public void key_direct() {
        final Key key = impl.createKey(reflect.field("flag"));
        assertNotNull(key);
        assertTrue(key instanceof Key.Direct);
        assertEquals(Byte.TYPE, key.type());
    }

    @Test
    public void key_named() {
        final Key key = impl.createKey(reflect.field("named"));
        assertNotNull(key);
        assertTrue(key instanceof Key.Named);
        assertEquals("a-long-list", ((Key.Named) key).name());
        assertEquals(new TypeToken<List<Long>>() {
        }.getType(), key.type());
    }

    @Test
    public void key_qualifier() {
        final Key key = impl.createKey(reflect.field("t"));
        assertNotNull(key);
        assertTrue(key instanceof Key.Qualified);
        assertEquals(MyQual.class, ((Key.Qualified) key).qualifier());
        assertEquals(T.class, key.type());
    }
}