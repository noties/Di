package ru.noties.di;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ModuleBindingBuilder {

    @SuppressWarnings("UnusedReturnValue")
    interface Qualifiers extends ModuleBindingBuilder {

        @NonNull
        Modifiers named(@NonNull String name);

        @NonNull
        Modifiers qualifier(@NonNull Class<? extends Annotation> qualifier);
    }

    @SuppressWarnings("UnusedReturnValue")
    interface Modifiers extends ModuleBindingBuilder {

        @NonNull
        Modifiers asLazy();

        @NonNull
        Modifiers asSingleton();

        @NonNull
        Modifiers asProvider();
    }

    interface QualifiersOrModifiers extends Qualifiers, Modifiers {
    }


    @SuppressWarnings("UnusedReturnValue")
    interface Typed<T> extends ModuleBindingBuilder, Qualifiers, Modifiers {

        @NonNull
        QualifiersOrModifiers with(@NonNull Provider<? extends T> provider);

        @NonNull
        QualifiersOrModifiers as(@NonNull Class<? extends T> type);
    }

    @SuppressWarnings("UnusedReturnValue")
    interface Raw extends ModuleBindingBuilder, Qualifiers, Modifiers {

        @NonNull
        QualifiersOrModifiers with(@NonNull Provider provider);

        @NonNull
        QualifiersOrModifiers as(@NonNull Type type);
    }
}
