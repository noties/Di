package ru.noties.di.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import javax.inject.Inject;
import javax.inject.Named;

import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Provider;
import ru.noties.di.app.injector.FragmentInjector;
import ru.noties.lazy.Lazy;

public class MainActivity extends FragmentActivity implements Di.Service {

    @Inject
    private Apple apple;

    @Inject
    private Banana banana;

    @Inject
    private Lazy<Grape> grapeLazy;

    @Inject
    private Provider<Apple> appleProvider;

    @Inject
    private Lazy<Provider<Banana>> bananaProviderLazy;

    @Inject
    private Berry berry;

    @Inject
    @Named("blue")
    private Berry namedBerry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(Window.ID_ANDROID_CONTENT) == null) {
            manager.beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, new MainFragment())
                    .commit();
        }
    }

    @Override
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
        // maybe allow optional module? with inject?
        di.fork("MainActivity")
                .inject(this)
                .accept(d -> FragmentInjector.init(getSupportFragmentManager(), d));
        Debug.i(apple, banana, grapeLazy.get());
        Debug.i(appleProvider);
        Debug.i(appleProvider.provide());
        Debug.i(bananaProviderLazy, bananaProviderLazy.get(), bananaProviderLazy.get().provide());
        Debug.i(berry, namedBerry);
    }
}
