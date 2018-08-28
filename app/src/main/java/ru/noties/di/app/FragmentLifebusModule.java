package ru.noties.di.app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import ru.noties.di.Module;
import ru.noties.di.reflect.TypeToken;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.fragment.FragmentEvent;
import ru.noties.lifebus.fragment.FragmentLifebus;

public class FragmentLifebusModule extends Module {

    private final Fragment fragment;

    public FragmentLifebusModule(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void configure() {

        bind(new TypeToken<Lifebus<FragmentEvent>>() {
        }.getType())
                .with(() -> FragmentLifebus.create(fragment))
                .asSingleton();
    }
}
