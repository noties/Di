package ru.noties.di;

import android.support.annotation.NonNull;

public class Configuration {

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    private final boolean allowInheritance;
    private final Logger logger;
    private final boolean disableImplicitDependencies;

    Configuration(@NonNull Builder builder) {
        this.allowInheritance = builder.allowInheritance;
        this.logger = builder.logger;
        this.disableImplicitDependencies = builder.disableImplicitDependencies;
    }

    public boolean allowInheritance() {
        return allowInheritance;
    }

    @NonNull
    public Logger logger() {
        return logger;
    }

    public boolean disableImplicitDependencies() {
        return disableImplicitDependencies;
    }

    public static class Builder {

        private boolean allowInheritance;
        private Logger logger;
        private boolean disableImplicitDependencies;

        @NonNull
        public Builder allowInheritance(boolean allowInheritance) {
            this.allowInheritance = allowInheritance;
            return this;
        }

        @NonNull
        public Builder logger(@NonNull Logger logger) {
            this.logger = logger;
            return this;
        }

        @NonNull
        public Builder disableImplicitDependencies(boolean disableImplicitDependencies) {
            this.disableImplicitDependencies = disableImplicitDependencies;
            return this;
        }

        @NonNull
        public Configuration build() {
            if (logger == null) {
                logger = Logger.noOp();
            }
            return new Configuration(this);
        }
    }
}
