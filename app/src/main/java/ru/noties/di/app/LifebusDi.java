package ru.noties.di.app;

import android.support.annotation.NonNull;

import ru.noties.di.Di;
import ru.noties.lifebus.Lifebus;

public abstract class LifebusDi {

    @NonNull
    public static <E extends Enum<E>> Di.Visitor closeOn(@NonNull Lifebus<E> lifebus, @NonNull E e) {
        return di -> lifebus.on(e, () -> {
            if (!di.isClosed()) {
                di.close();
            }
        });
    }

    private LifebusDi() {
    }
}
