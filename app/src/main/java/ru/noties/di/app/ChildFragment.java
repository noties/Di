package ru.noties.di.app;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.di.Di;

public class ChildFragment extends Fragment implements Di.Service {

    @Inject
    private Activity activity;

    @Override
    public void init(@NonNull Di di) {

        di.fork("ChildFragment")
                .inject(this);

        Debug.i(activity);
    }
}
