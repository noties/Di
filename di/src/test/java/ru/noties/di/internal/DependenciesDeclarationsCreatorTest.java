package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import ru.noties.di.Key;
import ru.noties.di.internal.DependenciesDeclarationsCreator.Impl;
import ru.noties.di.internal.DependenciesDeclarationsCreator.InheritanceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.noties.di.internal.TestUtils.with;

public class DependenciesDeclarationsCreatorTest {

    private Impl impl;
    private InheritanceImpl inheritanceImpl;

    @Before
    public void before() {
        impl = new Impl(new KeyCreator.Impl());
        inheritanceImpl = new InheritanceImpl(new KeyCreator.Impl());
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MyQual {

    }

    private static class T {

        @Inject
        private byte b;

        private boolean bool;

        @Inject
        @Named("too-short")
        private short s;

        @Inject
        @MyQual
        private int i;

        @Named("i-m-not-there")
        private long l;

        @MyQual
        private float f;

        private double d;
    }

    private static class T2 extends T {

        @Inject
        private TestUtils tu;
    }

    private static class T3 {

    }

    @Test
    public void test() {

        final Reflect reflect = new Reflect(T.class);

        final List<Map<Field, Key>> list = Arrays.asList(
                impl.create(T.class),
                inheritanceImpl.create(T.class)
        );

        for (final Map<Field, Key> map : list) {

            assertEquals(3, map.size());

            with(reflect.field("b"), new TestUtils.Action<Field>() {
                @Override
                public void apply(@NonNull Field field) {
                    final Key key = map.get(field);
                    assertNotNull(key);
                    assertEquals(Key.of(Byte.TYPE), key);
                }
            });

            with(reflect.field("s"), new TestUtils.Action<Field>() {
                @Override
                public void apply(@NonNull Field field) {
                    final Key key = map.get(field);
                    assertNotNull(key);
                    assertEquals(Key.of(Short.TYPE, "too-short"), key);
                }
            });

            with(reflect.field("i"), new TestUtils.Action<Field>() {
                @Override
                public void apply(@NonNull Field field) {
                    final Key key = map.get(field);
                    assertNotNull(key);
                    assertEquals(Key.of(Integer.TYPE, MyQual.class), key);
                }
            });
        }
    }

    @Test
    public void no_inheritance() {
        final Reflect reflect = new Reflect(T2.class);
        final Map<Field, Key> map = impl.create(T2.class);
        assertEquals(1, map.size());
        with(reflect.field("tu"), new TestUtils.Action<Field>() {
            @Override
            public void apply(@NonNull Field field) {
                final Key key = map.get(field);
                assertNotNull(key);
                assertEquals(Key.of(TestUtils.class), key);
            }
        });
    }

    @Test
    public void inheritance() {

        final Reflect reflect1 = new Reflect(T.class);
        final Reflect reflect2 = new Reflect(T2.class);

        final Map<Field, Key> map = inheritanceImpl.create(T2.class);
        assertEquals(4, map.size());

        with(reflect2.field("tu"), new TestUtils.Action<Field>() {
            @Override
            public void apply(@NonNull Field field) {
                final Key key = map.get(field);
                assertNotNull(key);
                assertEquals(Key.of(TestUtils.class), key);
            }
        });

        with(reflect1.field("b"), new TestUtils.Action<Field>() {
            @Override
            public void apply(@NonNull Field field) {
                final Key key = map.get(field);
                assertNotNull(key);
                assertEquals(Key.of(Byte.TYPE), key);
            }
        });

        with(reflect1.field("s"), new TestUtils.Action<Field>() {
            @Override
            public void apply(@NonNull Field field) {
                final Key key = map.get(field);
                assertNotNull(key);
                assertEquals(Key.of(Short.TYPE, "too-short"), key);
            }
        });

        with(reflect1.field("i"), new TestUtils.Action<Field>() {
            @Override
            public void apply(@NonNull Field field) {
                final Key key = map.get(field);
                assertNotNull(key);
                assertEquals(Key.of(Integer.TYPE, MyQual.class), key);
            }
        });
    }

    @Test
    public void no_injections() {
        final Map<Field, Key> map1 = impl.create(T3.class);
        final Map<Field, Key> map2 = inheritanceImpl.create(T3.class);
        assertEquals(0, map1.size());
        assertEquals(0, map2.size());
    }
}