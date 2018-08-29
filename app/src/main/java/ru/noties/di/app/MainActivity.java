package ru.noties.di.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import ru.noties.debug.Debug;
import ru.noties.di.Di;
import ru.noties.di.Module;
import ru.noties.di.android.FragmentInjector;
import ru.noties.lazy.Lazy;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.activity.ActivityEvent;

public class MainActivity extends FragmentActivity implements Di.Service {

    @Inject
    private Lifebus<ActivityEvent> lifebus;

    @Inject
    private Lifebus<ActivityEvent> lifebus2;

    @Inject
    private Banana banana;

    @Inject
    private Lazy<Banana> bananaLazy;

    @Inject
    @Named("banana-name")
    private Banana bananaNamed;

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface MyQualifier {
    }

    @Inject
    @MyQualifier
    private Banana bananaQualified;

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
                        // those are different bindings
                        bind(Banana.class).asLazy();
                        bind(Banana.class).named("banana-name");
                        bind(Banana.class).qualifier(MyQualifier.class);
                    }
                })
                .inject(this)
                .accept(FragmentInjector.init(getSupportFragmentManager()))
                .accept(LifebusDi.closeOn(lifebus, ActivityEvent.DESTROY));

        final long end = System.nanoTime();
        Debug.i("took: %d ns, %d ms", (end - start), (end - start) / 1000_000);
        Debug.i(lifebus, lifebus2);
        Debug.i(banana, bananaLazy, bananaNamed, bananaQualified);
    }
}
