package ru.noties.di.internal;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

abstract class MapUtils {

    @NonNull
    static <K, V> Map<K, V> freeze(@NonNull Map<K, V> map) {
        final Map<K, V> out;
        if (map.size() == 0) {
            out = Collections.emptyMap();
        } else {
            out = Collections.unmodifiableMap(map);
        }
        return out;
    }

    private MapUtils() {
    }
}
