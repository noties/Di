package ru.noties.di.app;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.di.OnInjected;

public class Banana implements OnInjected {

    @Inject
    private Apple apple;

    @Inject
    Banana() {

    }

    @Override
    public String toString() {
        return "Banana{" +
                "apple=" + apple +
                '}';
    }

    @Override
    public void onInjected() {
        Debug.e(apple);
    }
}
