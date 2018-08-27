package ru.noties.di;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Binding {

    @SuppressWarnings("UnusedReturnValue")
    interface Qualifiers extends Binding {

        @NonNull
        Modifiers named(@NonNull String name);

        @NonNull
        Modifiers qualifier(@NonNull Class<? extends Annotation> qualifier);
    }

    @SuppressWarnings("UnusedReturnValue")
    interface Modifiers extends Binding {

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
    interface Typed<T> extends Binding, Qualifiers, Modifiers {

        @NonNull
        QualifiersOrModifiers with(@NonNull Provider<? extends T> provider);

        @NonNull
        QualifiersOrModifiers as(@NonNull Class<? extends T> type);
    }

    @SuppressWarnings("UnusedReturnValue")
    interface Raw extends Binding, Qualifiers, Modifiers {

        @NonNull
        QualifiersOrModifiers with(@NonNull Provider provider);

        @NonNull
        QualifiersOrModifiers as(@NonNull Type type);
    }
}
