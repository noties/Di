package ru.noties.di.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.noties.di.Di;

public abstract class ActivityInjector {

    @NonNull
    public static Di.Visitor init(@NonNull final Application application) {
        return new Di.Visitor() {
            @Override
            public void visit(@NonNull final Di di) {
                application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        if (activity instanceof Di.Service) {
                            ((Di.Service) activity).init(di);
                        }
                    }
                });
            }
        };
    }

    private ActivityInjector() {
    }

    private static abstract class ActivityLifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
