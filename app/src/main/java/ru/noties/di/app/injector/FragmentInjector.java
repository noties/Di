package ru.noties.di.app.injector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.noties.di.Di;

public abstract class FragmentInjector {

    public static void init(@NonNull FragmentManager manager, @NonNull Di di) {

        // todo: we also might want to query for already attached fragments here
        //  and inject them if they are expecting us to do it

        manager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
                if (f instanceof Di.Service) {
                    ((Di.Service) f).init(di);
                }
            }

//            @Override
//            public void onFragmentDetached(FragmentManager fm, Fragment f) {
//                if (f instanceof Di.Service) {
//                    // todo: release created instance
//                }
//            }
        }, false); // <- false, so child fragments are created by holders
    }

    private FragmentInjector() {
    }
}
