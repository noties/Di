package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import ru.noties.di.Di;
import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.OnInjected;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.noties.di.internal.TestUtils.with;

public class FieldInjectionContributorTest {

    private static class PrivateConstructor {
        private PrivateConstructor() {
        }
    }

    private static class NoEmptyConstructor {
        public NoEmptyConstructor(byte b) {

        }
    }

    // falls into a category on non-empty constructor
    private class MemberClass {
        public MemberClass() {

        }
    }

    private static abstract class AbstractClass {
        public AbstractClass() {

        }
    }

    private static class ConstructorThrows {
        public ConstructorThrows() {
            throw new RuntimeException();
        }
    }

    private static class StaticThrows {
        static {
            if (true) {
                throw new RuntimeException();
            }
        }

        public StaticThrows() {

        }
    }

    @Test
    public void cannot_create() {

        // not accessible constructor?
        // no empty constructor?
        // member class non-static
        // abstract class
        // constructor throws exception
        // static init block throws (or static variable)

        final Class[] classes = {
                PrivateConstructor.class,
                AbstractClass.class,
                ConstructorThrows.class,
                StaticThrows.class
        };

        for (Class cl : classes) {
            final Reflect reflect = new Reflect(cl);
            final FieldInjectionContributor contributor =
                    new FieldInjectionContributor(reflect.constructor(), Collections.<Field, Key>emptyMap());
            try {
                contributor.contribute(null);
                assertTrue(false);
            } catch (DiException e) {
                assertTrue(e.getMessage(), e.getMessage().contains("Cannot create an instance of class"));
            }
        }

        with(new Reflect(NoEmptyConstructor.class), new TestUtils.Action<Reflect>() {
            @Override
            public void apply(@NonNull Reflect reflect) {
                final FieldInjectionContributor contributor =
                        new FieldInjectionContributor(reflect.constructor(byte.class), Collections.<Field, Key>emptyMap());
                try {
                    contributor.contribute(null);
                    assertTrue(false);
                } catch (DiException e) {
                    assertTrue(e.getMessage(), e.getMessage().contains("Cannot create an instance of class"));
                }
            }
        });

        with(new Reflect(MemberClass.class), new TestUtils.Action<Reflect>() {
            @Override
            public void apply(@NonNull Reflect reflect) {
                final FieldInjectionContributor contributor =
                        new FieldInjectionContributor(reflect.constructor(FieldInjectionContributorTest.class), Collections.<Field, Key>emptyMap());
                try {
                    contributor.contribute(null);
                    assertTrue(false);
                } catch (DiException e) {
                    assertTrue(e.getMessage(), e.getMessage().contains("Cannot create an instance of class"));
                }
            }
        });
    }

    private static class PrivateFields {

        private String s;

        public PrivateFields() {
        }
    }

    private static class FinalFields {
        private final int i = 0;

        public FinalFields() {
        }
    }

    private static class BadType {
        public String s;

        public BadType() {
        }
    }

    @Test
    public void cannot_set() {

        // not accessible or final
        // wrong type
        // if called with null receiver for instance field
        // if static field is set and static init block throws

        final class Throws {
            void check(@NonNull FieldInjectionContributor contributor, @NonNull Di di) {
                try {
                    contributor.contribute(di);
                    assertTrue(false);
                } catch (DiException e) {
                    assertTrue(e.getMessage(), e.getMessage().contains("Cannot inject "));
                }
            }
        }
        final Throws t = new Throws();

        with(new Reflect(PrivateFields.class), new TestUtils.Action<Reflect>() {
            @Override
            public void apply(@NonNull Reflect reflect) {
                final Map<Field, Key> fields = Collections.singletonMap(reflect.field("s"), Key.of(String.class));
                final FieldInjectionContributor contributor =
                        new FieldInjectionContributor(reflect.constructor(), fields);
                t.check(contributor, mock(Di.class));
            }
        });

        with(new Reflect(FinalFields.class), new TestUtils.Action<Reflect>() {
            @Override
            public void apply(@NonNull Reflect reflect) {
                final Map<Field, Key> fields = Collections.singletonMap(reflect.field("i"), Key.of(Integer.TYPE));
                final FieldInjectionContributor contributor =
                        new FieldInjectionContributor(reflect.constructor(), fields);
                final Di di = mock(Di.class);
                when(di.get(any(Key.class))).thenReturn(2);
                t.check(contributor, di);
            }
        });

        with(new Reflect(BadType.class), new TestUtils.Action<Reflect>() {
            @Override
            public void apply(@NonNull Reflect reflect) {
                final Map<Field, Key> fields = Collections.singletonMap(reflect.field("s"), Key.of(String.class));
                final FieldInjectionContributor contributor =
                        new FieldInjectionContributor(reflect.constructor(), fields);
                final Di di = mock(Di.class);
                when(di.get(any(Key.class))).thenReturn(123);
                t.check(contributor, di);
            }
        });
    }

    public static class AllSet {

        @Inject
        public String s;

        @Inject
        public Boolean b;

        public AllSet() {
        }
    }

    @Test
    public void all_set() {

        final Reflect reflect = new Reflect(AllSet.class);
        final Map<Field, Key> fields = new HashMap<Field, Key>() {{
            put(reflect.field("s"), Key.of(String.class));
            put(reflect.field("b"), Key.of(Boolean.class));
        }};

        final FieldInjectionContributor contributor =
                new FieldInjectionContributor(reflect.constructor(), fields);

        final Di di = mock(Di.class);
        when(di.get(Key.of(String.class))).thenReturn("hello-string!");
        when(di.get(Key.of(Boolean.class))).thenReturn(Boolean.TRUE);

        final AllSet allSet = (AllSet) contributor.contribute(di);
        assertEquals("s", "hello-string!", allSet.s);
        assertEquals("b", Boolean.TRUE, allSet.b);
    }

    public static class OnInjectedClass implements OnInjected {

        private final AtomicInteger count = new AtomicInteger();

        public OnInjectedClass() {

        }

        @Override
        public void onInjected() {
            count.incrementAndGet();
        }
    }

    @Test
    public void on_injected() {

        final Constructor constructor = new Reflect(OnInjectedClass.class).constructor();
        final FieldInjectionContributor contributor = new FieldInjectionContributor(constructor, Collections.<Field, Key>emptyMap());
        final OnInjectedClass cl = (OnInjectedClass) contributor.contribute(null);

        Assert.assertEquals(1, cl.count.get());
    }
}