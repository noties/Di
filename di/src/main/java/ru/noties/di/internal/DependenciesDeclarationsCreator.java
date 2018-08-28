package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.noties.di.Key;

abstract class DependenciesDeclarationsCreator {

    @NonNull
    abstract Map<Field, Key> create(@NonNull Class cl);

    @NonNull
    static DependenciesDeclarationsCreator create(boolean allowInheritance, @NonNull KeyCreator keyCreator) {
        return allowInheritance
                ? new InheritanceImpl(keyCreator)
                : new Impl(keyCreator);
    }

    static class Impl extends DependenciesDeclarationsCreator {

        private final KeyCreator keyCreator;
        final Map<Class, Map<Field, Key>> cache = new HashMap<>(3);

        Impl(@NonNull KeyCreator keyCreator) {
            this.keyCreator = keyCreator;
        }

        @NonNull
        @Override
        Map<Field, Key> create(@NonNull Class cl) {

            synchronized (cache) {

                Map<Field, Key> map = cache.get(cl);

                if (map == null) {
                    map = MapUtils.freeze(obtain(cl));
                    cache.put(cl, map);
                }

                return map;
            }
        }

        @NonNull
        Map<Field, Key> obtain(@NonNull Class cl) {

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

            return map;
        }
    }

    static class InheritanceImpl extends Impl {

        InheritanceImpl(@NonNull KeyCreator keyCreator) {
            super(keyCreator);
        }

        @NonNull
        @Override
        Map<Field, Key> create(@NonNull Class cl) {

            synchronized (cache) {

                Map<Field, Key> map = cache.get(cl);

                if (map == null) {

                    final List<InheritancePair> list = new ArrayList<>(3);

                    while (!cl.equals(Object.class)) {

                        map = cache.get(cl);

                        if (map == null) {
                            map = obtain(cl);
                            list.add(new InheritancePair(cl, map));
                        } else {
                            list.add(null);
                        }

                        InheritancePair pair;

                        // iterate up-ward and add fields from super class to children
                        for (int i = list.size() - 2; i > -1; i--) {
                            pair = list.get(i);
                            if (pair != null) {
                                pair.map.putAll(map);
                            }
                        }

                        cl = cl.getSuperclass();
                    }

                    map = MapUtils.freeze(list.get(0).map);

                    // cache data
                    for (InheritancePair pair : list) {
                        if (pair != null) {
                            cache.put(pair.type, MapUtils.freeze(pair.map));
                        }
                    }
                }

                return map;
            }
        }

        private static class InheritancePair {

            final Class type;
            final Map<Field, Key> map;

            InheritancePair(@NonNull Class type, @NonNull Map<Field, Key> map) {
                this.type = type;
                this.map = map;
            }
        }
    }
}
