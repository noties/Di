package ru.noties.di.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.noties.di.Di;

public abstract class FragmentInjector {

    @NonNull
    public static Di.Visitor init(@NonNull final FragmentManager manager) {
        return new Di.Visitor() {
            @Override
            public void visit(@NonNull final Di di) {

                manager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
                        if (f instanceof Di.Service) {
                            ((Di.Service) f).init(di);
                        }
                    }
                }, false); // <- false, so child fragments are handled by parents
            }
        };
    }

    private FragmentInjector() {
    }
}
