package ru.noties.di.internal;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.Collections;

import ru.noties.di.Di;
import ru.noties.di.Key;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImplicitProviderCreatorTest {

    @Test
    public void test() {

        final ImplicitKeyValidator implicitKeyValidator = mock(ImplicitKeyValidator.class);
        final InjectConstructorFinder injectConstructorFinder = mock(InjectConstructorFinder.class);
        final DependenciesDeclarationsCreator dependenciesDeclarationsCreator = mock(DependenciesDeclarationsCreator.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Boolean.TRUE;
            }
        }).when(implicitKeyValidator).validate(any(Key.class));

        when(injectConstructorFinder.find(any(Class.class)))
                .thenReturn(null);

        when(dependenciesDeclarationsCreator.create(any(Class.class)))
                .thenReturn(Collections.<Field, Key>emptyMap());

        final ImplicitProviderCreator.Impl impl = new ImplicitProviderCreator.Impl(
                implicitKeyValidator,
                injectConstructorFinder,
                dependenciesDeclarationsCreator
        );

        final Di.Contributor contributor = impl.create(Key.of(String.class));
        assertNotNull(contributor);

        verify(implicitKeyValidator, times(1)).validate(any(Key.class));
        verify(injectConstructorFinder, times(1)).find(any(Class.class));
        verify(dependenciesDeclarationsCreator, times(1)).create(any(Class.class));
    }
}