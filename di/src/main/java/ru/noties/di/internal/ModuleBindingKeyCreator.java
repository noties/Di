package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import ru.noties.di.Key;
import ru.noties.di.ModuleBinding;
import ru.noties.di.Provider;
import ru.noties.di.reflect.ParameterizedTypeImpl;
import ru.noties.lazy.Lazy;

abstract class ModuleBindingKeyCreator {

    @NonNull
    abstract Key create(@NonNull ModuleBinding binding);

    @NonNull
    static ModuleBindingKeyCreator create() {
        return new Impl();
    }

    static class Impl extends ModuleBindingKeyCreator {

        @NonNull
        @Override
        Key create(@NonNull ModuleBinding binding) {

            // let's not forget about lazy and provider
            // Lazy<Contributor<T>>, but not the other way <Contributor<Lazy>> (anyway doesn't make much sense)
            //               ^ this one will be hard to achieve with simple TypeToken
            //                 we will have to create own ParametrizedType

            final Key key;

            Type type = binding.bindType();

            // todo: this might seem to be a bit weird
            //  we do not check additionally if type is already lazy/provider
            //  but we assume that users won't use TypeToken functionality instead
            //  of a simple `asLazy` call

            if (binding.isProvider()) {
                type = new ParameterizedTypeImpl(Provider.class, null, type);
            }

            if (binding.isLazy()) {
                type = new ParameterizedTypeImpl(Lazy.class, null, type);
            }

            final String named = binding.named();
            final Class<? extends Annotation> qualifier = binding.qualifier();

            if (named != null
                    && named.length() > 0) {
                key = Key.of(type, named);
            } else if (qualifier != null) {
                key = Key.of(type, qualifier);
            } else {
                key = Key.of(type);
            }

            return key;
        }
    }
}
