package ru.noties.di.app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import javax.inject.Inject;
import javax.inject.Named;

import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Module;
import ru.noties.di.Provider;
import ru.noties.di.app.injector.FragmentInjector;
import ru.noties.lazy.Lazy;

public class MainFragment extends Fragment implements Di.Service {

    // apple here is implicit dependency that lives only in this scope (and children)
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
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
        di
                .fork("MainFragment")
                .inject(this)
                .accept(d -> FragmentInjector.init(getChildFragmentManager(), d));
        Debug.i(apple, banana, grapeLazy.get());
        Debug.i(appleProvider);
        Debug.i(appleProvider.provide());
        Debug.i(bananaProviderLazy, bananaProviderLazy.get(), bananaProviderLazy.get().provide());
        Debug.i(berry, namedBerry);
    }
}
