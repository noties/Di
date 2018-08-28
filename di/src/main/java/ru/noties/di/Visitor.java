package ru.noties.di;

import android.support.annotation.NonNull;

public interface Visitor<T> {
    void visit(@NonNull T t);
}
