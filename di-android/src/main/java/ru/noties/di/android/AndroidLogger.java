package ru.noties.di.android;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Locale;

import ru.noties.di.Logger;

public abstract class AndroidLogger extends Logger {

    @NonNull
    public static Logger create(boolean canLog) {
        return canLog
                ? new Impl()
                : Logger.noOp();
    }

    protected static class Impl extends AndroidLogger {

        private static final String PACKAGE = "ru.noties.di.";

        @Override
        public void log(@NonNull String tag, @NonNull String message) {
            Log.i(tag(), tag + ": " + message);
        }

        @Override
        public void log(@NonNull String tag, @NonNull String message, Object... args) {
            log(tag, String.format(Locale.US, message, args));
        }

        @Override
        public boolean canLog() {
            return true;
        }

        @NonNull
        protected static String tag() {

            // here we must find first non-library call

            final String out;

            final StackTraceElement[] elements = new Throwable().getStackTrace();
            final int length = elements != null
                    ? elements.length
                    : 0;

            if (length != 0) {

                StackTraceElement element = null;

                String className;

                final int offset = PACKAGE.length();

                for (int i = 0; i < length; i++) {

                    element = elements[i];
                    className = element.getClassName();

                    // so, this element is a class in
                    // * ru.noties.di.
                    // * ru.noties.di.internal
                    // * ru.noties.di.reflect
                    // * ru.noties.di.android
                    // it should be ignored

                    if (className.startsWith(PACKAGE)) {
                        // if next char is uppercase we assume that it's a class
                        if (Character.isUpperCase(className.charAt(offset))
                                || className.startsWith("internal.", offset)
                                || className.startsWith("reflect.", offset)
                                || className.startsWith("android.", offset)) {
                            element = null;
                            continue;
                        }
                    }

                    break;
                }

                if (element == null) {
                    out = "?";
                } else {
                    out = String.format(
                            Locale.US,
                            "%1$s(%2$s:%3$d)",
                            element.getMethodName(),
                            element.getFileName(),
                            element.getLineNumber()
                    );
                }
            } else {
                out = "?";
            }

            return out;
        }
    }
}
