package ru.noties.di.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import javax.inject.Inject;

import ru.noties.di.Di;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.view.ViewEvent;
import ru.noties.lifebus.view.ViewLifebus;

public class AppBar extends LinearLayout implements Di.Service {

    @Inject
    private Navigator navigator;

    public AppBar(Context context) {
        super(context);
        init(context, null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        setOrientation(VERTICAL);

        inflate(context, R.layout.view_app_bar, this);

        findViewById(R.id.app_bar_home).setOnClickListener(v -> navigator.goBack());
    }

    @Override
    public void init(@NonNull Di di) {

        final Lifebus<ViewEvent> lifebus = ViewLifebus.create(this);

        di
                .fork("AppBar")
                .inject(this)
                .accept(LifebusDi.closeOn(lifebus, ViewEvent.DETACH));
    }
}
