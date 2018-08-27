package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.OnInjected;

class FieldInjectionProvider implements Di.Provider {

    private final Constructor constructor;
    private final Map<Field, Key> fields;

    FieldInjectionProvider(
            @NonNull Constructor constructor,
            @NonNull Map<Field, Key> fields) {
        this.constructor = constructor;
        this.fields = fields;
    }

    @NonNull
    @Override
    public Object provide(@NonNull Di di) {

        Object o;
        try {
            o = constructor.newInstance();
        } catch (Throwable t) {
            throw DiException.halt(t, "Cannot create an instance of class: %s",
                    constructor.getDeclaringClass().getName());
        }

        Object value;

        for (Map.Entry<Field, Key> entry : fields.entrySet()) {
            value = di.get(entry.getValue());
            try {
                entry.getKey().set(o, value);
            } catch (Throwable t) {
                throw DiException.halt(t, "Cannot inject %s#%s with value: %s",
                        entry.getKey().getDeclaringClass().getName(), entry.getKey().getName(), value);
            }
        }

        if (o instanceof OnInjected) {
            ((OnInjected) o).onInjected();
        }

        return o;
    }
}
