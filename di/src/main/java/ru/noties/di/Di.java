package ru.noties.di;

import android.support.annotation.NonNull;

import java.util.Collection;

public interface Di {

    @NonNull
    Di fork(@NonNull String id, Module... modules);

    @NonNull
    Di fork(@NonNull String id, @NonNull Collection<Module> modules);


    interface Service {
        void init(@NonNull Di di);
    }

    @NonNull
    Di inject(@NonNull Service who);

    interface Visitor {
        void visit(@NonNull Di di);
    }

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    Di accept(@NonNull Visitor visitor);

    // todo: rename this so there is no confusion with Provider<T>
    interface Provider {
        @NonNull
        Object provide(@NonNull Di di);
    }

    @NonNull
    <T> T get(@NonNull Key key);

    @NonNull
    String path();
}
