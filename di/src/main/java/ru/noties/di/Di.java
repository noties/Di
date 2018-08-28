package ru.noties.di;

import android.support.annotation.NonNull;

import java.util.Collection;

public interface Di {

    @NonNull
    DiCloseable fork(@NonNull String id, Module... modules);

    @NonNull
    DiCloseable fork(@NonNull String id, @NonNull Collection<Module> modules);


    interface Service {
        void init(@NonNull Di di);
    }

    @NonNull
    Di inject(@NonNull Service who);

    interface Contributor {
        @NonNull
        Object contribute(@NonNull Di di);
    }

    @NonNull
    <T> T get(@NonNull Key key);

    @NonNull
    String path();

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    Di accept(@NonNull Visitor<Di> visitor);
}
