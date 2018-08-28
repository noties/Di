package ru.noties.di.app;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.noties.di.Module;

public class AppModule extends Module {

    private final App app;

    AppModule(@NonNull App app) {
        this.app = app;
    }

    @Override
    public void configure() {

        bind(Application.class).with(() -> app).asSingleton();
    }
}
