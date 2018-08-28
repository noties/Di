package ru.noties.di.app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import ru.noties.debug.Debug;
import ru.noties.di.Di;

public class ChildFragment extends Fragment implements Di.Service {

    @Override
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
    }
}
