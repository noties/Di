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


    @Override
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
    }
}
