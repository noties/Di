package ru.noties.di.app;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Module;
import ru.noties.di.Provider;
import ru.noties.di.app.injector.ActivityInjector;
import ru.noties.di.internal.DiImpl;

public class App extends Application {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final Di di = di();

        ActivityInjector.init(this, di);
    }

    @NonNull
    private Di di() {
        return DiImpl.root("App", new AppModule(this), new Module() {
            @Override
            public void configure() {

            }
        });
    }
}
