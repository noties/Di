package ru.noties.di.internal;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ru.noties.di.Di;
import ru.noties.di.DiException;
import ru.noties.di.Provider;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl.BaseContributor;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl.LazyContributor;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl.ProviderBridgeContributor;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl.ProviderContributor;
import ru.noties.di.internal.ModuleBindingContributorCreator.Impl.SingletonContributor;
import ru.noties.di.reflect.ParameterizedTypeImpl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ModuleBindingContributorCreatorTest {

    private InjectConstructorFinder injectConstructorFinder;
    private DependenciesDeclarationsCreator dependenciesDeclarationsCreator;

    private Impl impl;

    @Before
    public void before() {
        injectConstructorFinder = mock(InjectConstructorFinder.class);
        dependenciesDeclarationsCreator = mock(DependenciesDeclarationsCreator.class);
        impl = new Impl(
                injectConstructorFinder,
                dependenciesDeclarationsCreator
        );
    }

    @Test
    public void no_provider_no_origin() {
        final Binding binding = new Binding(String.class);
        impl.create(binding);

        verify(injectConstructorFinder, times(1)).find(String.class);
        verify(dependenciesDeclarationsCreator, times(1)).create(String.class);
    }

    @Test
    public void no_provider_with_origin() {

        final Binding binding = new Binding(CharSequence.class) {{
            originType = String.class;
        }};

        impl.create(binding);

        verify(injectConstructorFinder, times(1)).find(String.class);
        verify(dependenciesDeclarationsCreator, times(1)).create(String.class);
    }

    @Test
    public void no_provider_type_not_class_throws() {
        final Binding binding = new Binding(new ParameterizedTypeImpl(List.class, null, String.class));
        try {
            impl.create(binding);
            assertTrue(false);
        } catch (DiException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Cannot instantiate an instance of:"));
        }
    }

    @Test
    public void field_injection_contributor() {
        // additionally wrap with singleton, provider, lazy
        final Binding binding = new Binding(CharSequence.class) {{
            provider = new Provider() {
                @NonNull
                @Override
                public Object provide() {
                    return null;
                }
            };
        }};
        assertContributor(binding, FieldInjectionContributor.class);
    }

    @Test
    public void bridge() {
        // additionally wrap with singleton, provider, lazy
        final Binding binding = new Binding(CharSequence.class) {{
            provider = new Provider() {
                @NonNull
                @Override
                public Object provide() {
                    return null;
                }
            };
        }};
        assertContributor(binding, ProviderBridgeContributor.class);
    }

    public void assertContributor(@NonNull Binding binding, @NonNull Class initial) {

        assertContributorTypes(impl.create(binding), initial);

        binding = new Binding(binding) {{
            isProvider = true;
        }};
        assertContributorTypes(
                impl.create(binding),
                ProviderContributor.class, initial
        );

        binding = new Binding(binding) {{
            isLazy = true;
        }};
        assertContributorTypes(
                impl.create(binding),
                LazyContributor.class, ProviderContributor.class, initial
        );

        binding = new Binding(binding) {{
            isSingleton = true;
        }};
        assertContributorTypes(
                impl.create(binding),
                SingletonContributor.class, LazyContributor.class, ProviderContributor.class, initial
        );
    }

    private void assertContributorTypes(@NonNull Di.Contributor contributor, Class... types) {

        final int length = types.length;
        assertTrue(length > 0);
        for (int i = 0; i < length; i++) {
            //noinspection unchecked
            types[i].isAssignableFrom(contributor.getClass());
            if (i != length - 1) {
                contributor = ((BaseContributor) contributor).parent;
            }
        }
    }
}