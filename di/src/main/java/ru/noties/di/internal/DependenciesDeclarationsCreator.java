package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ru.noties.di.Key;

abstract class DependenciesDeclarationsCreator {

    @NonNull
    abstract Map<Field, Key> create(@NonNull Class cl);

    static DependenciesDeclarationsCreator create(@NonNull KeyCreator keyCreator) {
        return new Impl(keyCreator);
    }

    static class Impl extends DependenciesDeclarationsCreator {

        private final KeyCreator keyCreator;
        private final Map<Class, Map<Field, Key>> cache = new HashMap<>(3);

        Impl(@NonNull KeyCreator keyCreator) {
            this.keyCreator = keyCreator;
        }

        @NonNull
        @Override
        Map<Field, Key> create(@NonNull Class cl) {

            synchronized (cache) {
                Map<Field, Key> map = cache.get(cl);
                if (map == null) {
                    map = obtain(cl);
                    cache.put(cl, map);
                }
                return map;
            }
        }

        @NonNull
        private Map<Field, Key> obtain(@NonNull Class cl) {

            final Map<Field, Key> map = new HashMap<>(3);

            final Field[] fields = cl.getDeclaredFields();

            Key key;

            for (Field field : fields) {
                field.setAccessible(true);
                key = keyCreator.createKey(field);
                if (key != null) {
                    map.put(field, key);
                }
            }

            return MapUtils.freeze(map);
        }
    }
}
