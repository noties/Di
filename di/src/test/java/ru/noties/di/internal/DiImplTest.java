package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ru.noties.di.Configuration;
import ru.noties.di.Di;
import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.Logger;
import ru.noties.di.Module;
import ru.noties.di.Provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiImplTest {

    private static class Implicit {

        @Inject
        Implicit() {

        }
    }

    @Test
    public void logs() {

        final class LoggerImpl extends Logger {

            private final List<String> tags = new ArrayList<>(3);

            @Override
            public void log(@NonNull String tag, @NonNull String message) {
                tags.add(tag);
            }

            @Override
            public void log(@NonNull String tag, @NonNull String message, Object... args) {
                tags.add(tag);
            }

            @Override
            public boolean canLog() {
                return true;
            }
        }
        final LoggerImpl logger = new LoggerImpl();

        final DiImpl impl = DiImpl.root(
                Configuration.builder().logger(logger).build(),
                "root",
                new Module() {
                    @Override
                    public void configure() {
                        bind(String.class).with(new Provider<String>() {
                            @NonNull
                            @Override
                            public String provide() {
                                return "hey-hey-ho-ho";
                            }
                        });
                    }
                }
        );

        // di-fork
        // di-close
        impl.fork("child").close();

        // di-inject
        impl.inject(mock(Di.Service.class));

        // di-get-explicit
        impl.get(Key.of(String.class));

        // di-get-implicit
        impl.get(Key.of(Implicit.class));

        final List<String> expected = Arrays.asList(
                "di-fork",
                "di-close",
                "di-inject",
                "di-get-explicit",
                "di-get-implicit"
        );

        assertEquals(expected, logger.tags);
    }

    @Test
    public void no_logs_if_canLog_false() {

        final Logger logger = mock(Logger.class);
        when(logger.canLog()).thenReturn(false);

        final DiImpl impl = DiImpl.root(
                Configuration.builder().logger(logger).build(),
                "root",
                new Module() {
                    @Override
                    public void configure() {
                        bind(String.class).with(new Provider<String>() {
                            @NonNull
                            @Override
                            public String provide() {
                                return "provided-string";
                            }
                        });
                    }
                }
        );

        // di-fork
        // di-close
        impl.fork("child").close();

        // di-inject
        impl.inject(mock(Di.Service.class));

        // di-get-explicit
        impl.get(Key.of(String.class));

        // di-get-implicit
        impl.get(Key.of(Implicit.class));

        verify(logger, times(0)).log(any(String.class), any(String.class));
        verify(logger, times(0)).log(any(String.class), any(String.class), any(Object[].class));
    }

    @Test
    public void all_operations_on_closed_throws() {

        // fork
        // inject
        // get (twice, explicit, then implicit)

        final DiImpl impl = DiImpl.root("root");

        assertFalse(impl.isClosed());
        assertEquals("/root", impl.path());

        final String message = "is closed";

        impl.close();

        assertTrue(impl.isClosed());
        assertEquals("/[X]root", impl.path());

        assertThrows(message, new Runnable() {
            @Override
            public void run() {
                impl.fork("fork");
            }
        });

        assertThrows(message, new Runnable() {
            @Override
            public void run() {
                impl.inject(new Di.Service() {
                    @Override
                    public void init(@NonNull Di di) {
                        throw new RuntimeException();
                    }
                });
            }
        });

        assertThrows(message, new Runnable() {
            @Override
            public void run() {
                impl.get(Key.of(String.class));
            }
        });
    }

    private static class Recursive {

        @Inject
        private Recursive recursive;

        @Inject
        public Recursive() {
        }
    }

    @Test
    public void recursive_injection_throws() {

        final DiImpl impl = DiImpl.root("root");
        assertThrows("Recursive injection for the key", new Runnable() {
            @Override
            public void run() {
                impl.get(Key.of(Recursive.class));
            }
        });
    }

    private static class ImplicitValue {

        @Inject
        ImplicitValue() {

        }
    }

    private static class ImplicitHolder implements Di.Service {

        @Inject
        ImplicitValue value;

        @Override
        public void init(@NonNull Di di) {
            throw new RuntimeException();
        }
    }

    @Test
    public void implicit_dependencies_disabled() {

        final DiImpl impl = DiImpl.root(
                Configuration.builder().disableImplicitDependencies(true).build(),
                "root"
        );

        assertThrows("Implicit dependencies are disabled", new Runnable() {
            @Override
            public void run() {
                impl.inject(new ImplicitHolder());
            }
        });
    }

    @Test
    public void close_closes_children() {

        final DiImpl impl = DiImpl.root("root");
        final Di child = impl.fork("child");

        assertFalse(impl.isClosed());
        assertFalse(child.isClosed());

        impl.close();

        assertTrue(child.isClosed());
        assertTrue(impl.isClosed());

        assertEquals("/[X]root/[X]child", child.path());
    }

    private void assertThrows(@NonNull String messageContains, @NonNull Runnable runnable) {
        try {
            runnable.run();
            assertTrue(false);
        } catch (DiException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(messageContains));
        }
    }
}