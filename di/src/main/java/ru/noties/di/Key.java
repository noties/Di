package ru.noties.di;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import ru.noties.di.reflect.Types;

// to identify single dependency
// used for lookups
public abstract class Key {

    @NonNull
    public static Key of(@NonNull Type type) {
        return new Direct(type);
    }

    @NonNull
    public static Key of(@NonNull Type type, @NonNull String name) {
        return new Named(type, name);
    }

    // here we won't be validating if supplied annotation has a `Qualifier` annotation
    @NonNull
    public static Key of(@NonNull Type type, @NonNull Class<? extends Annotation> qualifier) {
        return new Qualified(type, qualifier);
    }

    final Type type;

    Key(@NonNull Type type) {
        this.type = Types.canonicalize(type);
    }

    @NonNull
    public final Type type() {
        return type;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract String toString();


    public static class Direct extends Key {

        Direct(@NonNull Type type) {
            super(type);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Direct direct = (Direct) o;

            return type.equals(direct.type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public String toString() {
            return "Key.Direct{" +
                    type +
                    '}';
        }
    }

    public static class Named extends Key {

        private final String name;

        Named(@NonNull Type type, @NonNull String name) {
            super(type);
            this.name = name;
        }

        @NonNull
        public String name() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Named named = (Named) o;

            if (!type.equals(named.type)) return false;
            return name.equals(named.name);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Key.Named{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Qualified extends Key {

        private final Class<? extends Annotation> qualifier;

        Qualified(@NonNull Type type, @NonNull Class<? extends Annotation> qualifier) {
            super(type);
            this.qualifier = qualifier;
        }

        @NonNull
        public Class<? extends Annotation> qualifier() {
            return qualifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Qualified qualified = (Qualified) o;

            if (!type.equals(qualified.type)) return false;
            return qualifier.equals(qualified.qualifier);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + qualifier.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Key.Qualified{" +
                    "type=" + type +
                    ", qualifier=" + qualifier.getName() +
                    '}';
        }
    }
}
