package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ru.noties.di.DiException;
import ru.noties.di.internal.InjectConstructorFinder.Impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InjectConstructorFinderTest {

    private Impl impl;

    @Before
    public void before() {
        impl = new Impl();
    }

    @Test
    public void multiple_inject_constructors() {

        assertThrows(
                "Multiple constructors with @Inject annotation",
                new Runnable() {
                    @Override
                    public void run() {
                        impl.find(Multiple.class);
                    }
                }
        );
    }

    @Test
    public void multiple_constructors_single_inject() {
        assertNotNull(impl.find(SingleInjectMultiple.class));
    }

    @Test
    public void no_inject_constructors() {
        assertThrows(
                "No empty constructor with @Inject annotation found",
                new Runnable() {
                    @Override
                    public void run() {
                        impl.find(NoConstructors.class);
                    }
                }
        );
    }

    @Test
    public void inject_constructor_has_parameters() {
        assertThrows(
                "Constructor with @Inject annotation is not empty (has parameters)",
                new Runnable() {
                    @Override
                    public void run() {
                        impl.find(WithParameters.class);
                    }
                }
        );
    }

    @Test
    public void not_concrete_class() {

        final Map<String, Class> map = new HashMap<String, Class>() {{

            final class Local {
            }

            put("annotation", NonNull.class);
            put("anonymous class", this.getClass());
            put("array", String[].class);
            put("enum", SomeEnum.class);
            put("interface", CharSequence.class);
            put("local class", Local.class);
            put("abstract class", SomeAbstract.class);
            put("non-static member class", SomeNonStaticMember.class);
            put("primitive", int.class);

            // cannot create a synthetic class of this level.. 1.7
//            put("synthetic class", synthetic);
        }};

        for (final Map.Entry<String, Class> entry : map.entrySet()) {

            final String message = "Provided type cannot be instantiated, reason: " + entry.getKey();
            assertThrows(message, new Runnable() {
                @Override
                public void run() {
                    impl.find(entry.getValue());
                }
            });
        }
    }

    private void assertThrows(@NonNull String message, @NonNull Runnable runnable) {
        try {
            runnable.run();
            assertTrue(false);
        } catch (DiException e) {
            assertTrue(message + ", " + e.getMessage(), e.getMessage().contains(message));
        }
    }

    private enum SomeEnum {
        HEY
    }

    private static abstract class SomeAbstract {
    }

    private class SomeNonStaticMember {
    }

    private static class Multiple {

        @Inject
        Multiple() {

        }

        @Inject
        Multiple(byte b) {

        }
    }

    private static class SingleInjectMultiple {

        @Inject
        SingleInjectMultiple() {

        }

        SingleInjectMultiple(byte b) {

        }
    }

    private static class NoConstructors {

    }

    private static class WithParameters {

        @Inject
        WithParameters(byte b) {

        }
    }
}