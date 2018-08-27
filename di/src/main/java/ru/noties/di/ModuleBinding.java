package ru.noties.di;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ModuleBinding {

    @NonNull
    Type bindType();

    @Nullable
    Type originType();

    @Nullable
    Provider provider();

    @Nullable
    String named();

    @Nullable
    Class<? extends Annotation> qualifier();

    boolean isSingleton();

    boolean isLazy();

    boolean isProvider();
}
