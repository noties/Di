package ru.noties.di.app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.di.Di;

public class MainChildFragment extends Fragment implements Di.Service {

    @Inject
    private Apple apple;

    @Override
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
        di.inject(this);
        Debug.i(apple);
    }

    @Override
    public void onResume() {
        super.onResume();

        final FragmentManager manager = getChildFragmentManager();
        if (manager.findFragmentByTag("child") == null) {
            manager.beginTransaction()
                    .add(new MainChildFragment(), "child")
                    .commit();
        }
    }
}
