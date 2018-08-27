package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class ArrayUtils {

    @NonNull
    static <T> List<T> toList(@Nullable T[] array) {

        final List<T> list;

        final int length = array != null
                ? array.length
                : 0;

        if (length > 0) {
            list = new ArrayList<>(length);
            Collections.addAll(list, array);
        } else {
            list = Collections.emptyList();
        }

        return list;
    }

    private ArrayUtils() {
    }
}
