package ru.noties.di.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.noties.di.Di;
import ru.noties.di.Module;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.fragment.FragmentEvent;

public class MainFragment extends Fragment implements Di.Service {

    @Inject
    private Lifebus<FragmentEvent> lifebus;

    private Di di;

    @Override
    public void init(@NonNull Di di) {
        this.di = di
                .fork("MainFragment", new FragmentLifebusModule(this), new Module() {
                    @Override
                    public void configure() {

                        // parent dependency
                        final Navigator navigator = require(Navigator.class);

                        // override parent dependency, so all children in this scope
                        // will receive this navigator instead of parents
                        bind(Navigator.class).with(() -> () -> {
                            if (!true) {
                                // do something
                            } else {
                                // call parent dependency if we have no special handling here
                                navigator.goBack();
                            }
                        });
                    }
                })
                .inject(this)
                // can also init child fragment if we would use them
//                .accept(FragmentInjector.init(getChildFragmentManager()))
                .accept(LifebusDi.closeOn(lifebus, FragmentEvent.DETACH));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AppBar appBar = view.findViewById(R.id.app_bar);
        appBar.init(di);
    }
}
