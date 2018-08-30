package ru.noties.di.app;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import ru.noties.di.Module;
import ru.noties.di.reflect.TypeToken;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.activity.ActivityEvent;
import ru.noties.lifebus.activity.ActivityLifebus;

public class ActivityLifebusModule extends Module {

    private final Activity activity;

    public ActivityLifebusModule(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void configure() {

        // strictly speaking we do not need it here, but we could
        //  access parent dependencies like this
        // please note that will `require` a dependency from PARENT Di
        // if it's called when defining module for a root Di, then this one
        // will throw
        final Application application = require(Application.class);

        bind(new TypeToken<Lifebus<ActivityEvent>>() {
        }.getType())
                .with(() -> ActivityLifebus.create(application, activity))
                .asSingleton();

        bind(Activity.class).with(() -> activity).asSingleton();
    }
}
