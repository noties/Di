package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.noties.di.Configuration;
import ru.noties.di.Di;
import ru.noties.di.DiException;
import ru.noties.di.Key;
import ru.noties.di.Logger;
import ru.noties.di.Module;

// I would rather spend time accumulated from faster build-times on
// testing. And do it without having to wait until app is building (proper behavior in one run)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class DiImpl extends Di {

    // todo: we are setting isClosed flag after closing children.. should we do anything about it?
    // todo: move diexception to public
    // todo: additional android module that will test with proguard enabled
    //      do not forget to put it into different package of `ru.noties.di.` as it
    //      has a proguard rule
    // todo: https://stackoverflow.com/questions/6762012/suppress-variable-is-never-assigned-warning-in-intellij-idea#comment34257832_6762287

    @NonNull
    public static DiImpl root(@NonNull String id, Module... modules) {
        return root(id, ArrayUtils.toList(modules));
    }

    @NonNull
    public static DiImpl root(@NonNull String id, @NonNull Collection<Module> modules) {
        return root(
                Configuration.builder().build(),
                id,
                modules
        );
    }

    @NonNull
    public static DiImpl root(
            @NonNull Configuration configuration,
            @NonNull String id,
            Module... modules) {
        return root(
                configuration,
                id,
                ArrayUtils.toList(modules)
        );
    }

    @NonNull
    public static DiImpl root(
            @NonNull Configuration configuration,
            @NonNull String id,
            @NonNull Collection<Module> modules) {
        return new DiImpl(
                InternalDependencies.create(configuration.allowInheritance()),
                null,
                id,
                CollectionUtils.requireNoNulls(
                        "Cannot add `null` as a module",
                        modules
                ),
                configuration);
    }

    private final InternalDependencies dependencies;
    private final DiImpl parent;
    private final String id;
    private final Configuration configuration;
    private final Logger logger;

    // if explicit dependency is obtained from parent we cannot supply it our context
    //       as parent must not know about children and thus be created inside self
    private final Map<Key, Contributor> explicit;

    private final Set<Key> pendingInjections = Collections.synchronizedSet(new HashSet<Key>(3));

    private volatile boolean isClosed;

    private final List<Di> children = Collections.synchronizedList(new ArrayList<Di>(3));

    DiImpl(
            @NonNull InternalDependencies dependencies,
            @Nullable DiImpl parent,
            @NonNull String id,
            @NonNull Collection<Module> modules,
            @NonNull Configuration configuration) {
        this.dependencies = dependencies;
        this.parent = parent;
        this.id = id;
        this.configuration = configuration;
        this.logger = configuration.logger();
        this.explicit = dependencies.explicitBuilder().build(parent, modules);
    }

    @NonNull
    @Override
    public Di fork(@NonNull String id, Module... modules) {
        return fork(id, ArrayUtils.toList(modules));
    }

    @NonNull
    @Override
    public Di fork(@NonNull String id, @NonNull Collection<Module> modules) {

        // log: forking ${path()} -> ${path() + id}, modules: $modules
        if (logger.canLog()) {
            logger.log("di-fork", "Forking %1$s -> %1$s/%2$s, modules: %3$s", path(), id, modules);
        }

        checkState();

        final DiImpl impl = new DiImpl(
                dependencies,
                this,
                id,
                CollectionUtils.requireNoNulls("Cannot add `null` as a module", modules),
                configuration);

        // record child instance for later closing
        children.add(impl);

        return impl;
    }

    @NonNull
    @Override
    public Di inject(@NonNull Service who) {

        if (logger.canLog()) {
            logger.log("di-inject", "Injecting service `%s` with %s", who, path());
        }

        checkState();

        dependencies.serviceInjector().inject(this, who);

        return this;
    }

    @NonNull
    @Override
    public Di accept(@NonNull Visitor visitor) {
        visitor.visit(this);
        return this;
    }

    @NonNull
    public <T> T get(@NonNull Key key) {

        checkState();

        if (pendingInjections.contains(key)) {
            throw DiException.halt("%s, Recursive injection for the key: %s",
                    path(), key);
        }

        pendingInjections.add(key);
        try {

            Contributor contributor = null;

            DiImpl impl = this;

            while (impl != null) {
                contributor = impl.getExplicitContributor(key);
                if (contributor != null) {
                    break;
                }
                impl = impl.parent;
            }

            if (contributor != null) {

                if (logger.canLog()) {
                    logger.log("di-get-explicit", "Obtained explicit binding `%s` " +
                            "from: %s, this: %s", key, impl.path(), path());
                }

                //noinspection unchecked
                return (T) contributor.contribute(impl);
            }

            if (logger.canLog()) {
                logger.log("di-get-implicit", "Obtaining implicit binding `%s` " +
                        "from: %s", key, path());
            }

            checkState();

            if (configuration.disableImplicitDependencies()) {
                throw DiException.halt("Implicit dependencies are disabled, key: %s, " +
                        "this: %s", key, path());
            }

            contributor = dependencies.implicitProviderCreator().create(key);

            //noinspection unchecked
            return (T) contributor.contribute(this);

        } finally {
            pendingInjections.remove(key);
        }
    }

    @Nullable
    private Contributor getExplicitContributor(@NonNull Key key) {
        return explicit.get(key);
    }

    @NonNull
    public String path() {

        final StringBuilder builder = new StringBuilder();

        builder.append('/');
        if (isClosed) {
            builder.append("[X]");
        }
        builder.append(id);

        DiImpl parent = this.parent;
        while (parent != null) {
            builder.insert(0, parent.id);
            if (parent.isClosed) {
                builder.insert(0, "[X]");
            }
            builder.insert(0, '/');
            parent = parent.parent;
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "DiImpl(" + path() + ")";
    }

    @Override
    public void close() {

        if (!isClosed) {

            synchronized (children) {

                if (!isClosed) {

                    // we no longer need to reference them
                    for (Di child : children) {
                        if (!child.isClosed()) {
                            child.close();
                        }
                    }
                    children.clear();

                    isClosed = true;

                    if (logger.canLog()) {
                        logger.log("di-close", "Closing %s", path());
                    }

                    explicit.clear();
                }
            }
        }
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    private void checkState() {
        if (isClosed) {
            throw DiException.halt("%s: is closed", path());
        }
    }
}
