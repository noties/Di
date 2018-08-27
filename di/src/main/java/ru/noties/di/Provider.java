package ru.noties.di;

import android.support.annotation.NonNull;

public interface Provider<T> {

    @NonNull
    T provide();
}
