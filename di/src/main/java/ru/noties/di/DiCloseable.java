package ru.noties.di;

import android.support.annotation.NonNull;

public interface DiCloseable extends Di {

    void close();

    // will also validate that all possible parents of this instance is closed
    // so if there is at least one closed parent -> this instance is considered to be closed
    boolean isClosed();

    @NonNull
    @Override
    DiCloseable inject(@NonNull Service who);

    @NonNull
    @Override
    DiCloseable accept(@NonNull Visitor<Di> visitor);

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    DiCloseable acceptCloseable(@NonNull Visitor<DiCloseable> visitor);
}
