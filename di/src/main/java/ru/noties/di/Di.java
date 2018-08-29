package ru.noties.di;

import android.support.annotation.NonNull;

import java.util.Collection;

import ru.noties.di.internal.DiImpl;

public abstract class Di {


    public interface Service {
        void init(@NonNull Di di);
    }

    public interface Contributor {
        @NonNull
        Object contribute(@NonNull Di di);
    }

    public interface Visitor {
        void visit(@NonNull Di di);
    }


    @NonNull
    public static Di root(@NonNull String id, Module... modules) {
        return DiImpl.root(id, modules);
    }

    @NonNull
    public static Di root(@NonNull String id, @NonNull Collection<Module> modules) {
        return DiImpl.root(id, modules);
    }

    @NonNull
    public static Di root(
            @NonNull Configuration configuration,
            @NonNull String id,
            Module... modules) {
        return DiImpl.root(configuration, id, modules);
    }

    @NonNull
    public static Di root(
            @NonNull Configuration configuration,
            @NonNull String id,
            @NonNull Collection<Module> modules) {
        return DiImpl.root(
                configuration,
                id,
                modules
        );
    }

    @NonNull
    public abstract Di fork(@NonNull String id, Module... modules);

    @NonNull
    public abstract Di fork(@NonNull String id, @NonNull Collection<Module> modules);

    public abstract void close();

    public abstract boolean isClosed();

    @NonNull
    public abstract Di inject(@NonNull Service who);

    @NonNull
    public abstract <T> T get(@NonNull Key key);

    @NonNull
    public abstract String path();

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract Di accept(@NonNull Visitor visitor);
}
