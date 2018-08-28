package ru.noties.di.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.noties.di.Di;
import ru.noties.di.Visitor;

public abstract class FragmentInjector {

    @NonNull
    public static Visitor<Di> init(@NonNull final FragmentManager manager) {
        return new Visitor<Di>() {
            @Override
            public void visit(@NonNull final Di di) {

                manager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
                        if (f instanceof Di.Service) {
                            ((Di.Service) f).init(di);
                        }
                    }
                }, false); // <- false, so child fragments are created by holders
            }
        };
    }

    private FragmentInjector() {
    }
}
