package ru.noties.di.app;

import android.app.Application;
import android.support.annotation.NonNull;

import java.lang.reflect.Type;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.Provider;
import ru.noties.di.reflect.TypeToken;
import ru.noties.di.app.injector.ActivityInjector;
import ru.noties.di.internal.DiImpl;
import ru.noties.di.reflect.ParameterizedTypeImpl;
import ru.noties.lazy.Lazy;

public class App extends Application {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    private Lazy<Grape> grapeLazy;

    private Lazy<Provider<Banana>> bananaProviderLazy;

    @Override
    public void onCreate() {
        super.onCreate();

        final Di di  = di();

//        final Object o = di.get(Key.of(new TypeToken<Lazy<Grape>>(){}.getType()));
//        Debug.i(o);

        try {

            final Key key1 = Key.of(new TypeToken<Lazy<Provider<Banana>>>(){}.getType());
            final Key key2 = Key.of(new ParameterizedTypeImpl(Lazy.class, null, new Type[]{new ParameterizedTypeImpl(Provider.class, null, new Type[] {Banana.class})}));
            final Key key3 = Key.of(App.class.getDeclaredField("bananaProviderLazy").getGenericType());

            Debug.i(key1.hashCode(), key1, key1.type().getClass().getName());
            Debug.i(key2.hashCode(), key2, key2.type().getClass().getName());
            Debug.i(key3.hashCode(), key3, key3.type().getClass().getName());

        } catch (Throwable t) {
            Debug.e(t);
        }

        // for example, obtain root instance here (by definition all dependencies
        // that are @singleton, will have exactly one instance for the life of an application)
        ActivityInjector.init(this, di);
    }

    @NonNull
    private Di di() {
        return DiImpl.root("App", new Module() {
            @Override
            public void configure() {

                bind(Apple.class).asSingleton();

                bind(Grape.class)
                        .asLazy()
                        .asSingleton();

                bind(Apple.class)
                        .asProvider()
                        .asSingleton();

                bind(Banana.class)
                        .asLazy()
                        .asProvider()
                        .asSingleton();

                bind(Berry.class)
                        .named("blue")
                        .asSingleton();
            }
        });
    }
}
