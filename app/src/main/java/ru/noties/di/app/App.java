package ru.noties.di.app;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.di.Configuration;
import ru.noties.di.Di;
import ru.noties.di.android.ActivityInjector;
import ru.noties.di.android.AndroidLogger;
import ru.noties.di.internal.DiImpl;

public class App extends Application {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        di().accept(ActivityInjector.init(this));
    }

    @NonNull
    private Di di() {

        final Configuration configuration = Configuration.builder()
                .allowInheritance(false)
                .logger(AndroidLogger.create(true))
                .disableImplicitDependencies(false)
                .build();

        return Di.root(configuration, "App", new AppModule(this));
    }
}
