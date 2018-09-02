package ru.noties.di.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Module;
import ru.noties.di.android.FragmentInjector;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.activity.ActivityEvent;

public class MainActivity extends FragmentActivity implements Di.Service, Navigator {

    @Inject
    private Lifebus<ActivityEvent> lifebus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(Window.ID_ANDROID_CONTENT) == null) {
            manager.beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, new MainFragment())
                    .commit();
        }
    }

    @Override
    public void init(@NonNull Di di) {

        final long start = System.nanoTime();

        di
                .fork("MainActivity", new ActivityLifebusModule(this), new Module() {
                    @Override
                    public void configure() {
                        bind(Navigator.class).with(() -> MainActivity.this);
                    }
                })
                .inject(this)
                .accept(FragmentInjector.init(getSupportFragmentManager()))
                .accept(LifebusDi.closeOn(lifebus, ActivityEvent.DESTROY));

        final long end = System.nanoTime();
        Debug.i("took: %d ns, %d ms", (end - start), (end - start) / 1000_000);
        Debug.i(lifebus);
    }

    @Override
    public void goBack() {
        onBackPressed();
    }
}
