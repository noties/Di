package ru.noties.di.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.android.FragmentInjector;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.fragment.FragmentEvent;

public class MainFragment extends Fragment implements Di.Service {

    @Inject
    private Lifebus<FragmentEvent> lifebus;

    @Override
    public void init(@NonNull Di di) {
        Debug.i("di: %s", di);
        di.fork("MainFragment", new FragmentLifebusModule(this))
                .inject(this)
                .accept(FragmentInjector.init(getChildFragmentManager()))
                .acceptCloseable(diCloseable -> lifebus.on(FragmentEvent.DETACH, diCloseable::close));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (getChildFragmentManager().findFragmentByTag("child") == null) {
                getChildFragmentManager().beginTransaction()
                        .add(new ChildFragment(), "child")
                        .commit();
            }
        }, 2000L);
        return new TextView(container.getContext());
    }
}
