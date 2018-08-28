package ru.noties.di.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.noties.di.Di;
import ru.noties.di.DiCloseable;
import ru.noties.di.Key;
import ru.noties.di.Module;
import ru.noties.di.Visitor;

// I would rather spend time accumulated from faster build-times on
// testing. And do it without having to wait until app is building (proper behavior in one run)
public class DiImpl implements Di, DiCloseable {

    // todo: maybe provide a way to disable implicit dependencies?
    // todo: simple test module to validate that all dependencies can be satisfied
    // todo: design so that we can easily introduce annotation processor
    // todo: we can create id: `/parent-id1/parent-id2/id`
    //       we also can have a simple flag indicating if someone in the path is closed!
    //       `/parent/[X]parent-closed/id`
    // todo: maybe DiDebug(Di) -> to allow for runtime inspection?
    // todo: maybe make Di abstract and move this one in `internal` package
    // todo: maybe introduce `reflection` option when annotation processor is implemented (if of cause)
    // todo: proguard (https://github.com/zsoltherpai/feather/pull/11/files)
    // todo: inheritance (of injected objects?) as an config option maybe?
    // todo: add path to instance specific errors
    // todo: investigate self-container injection... to swap some implementation details?
    // todo: create logger interface & configuration
    // todo: wildcard reflect type (why not?)
    // todo: https://stackoverflow.com/questions/6762012/suppress-variable-is-never-assigned-warning-in-intellij-idea#comment34257832_6762287
    // todo: #close -> should clear explicit dependencies from children also...
    //      we should not rely on a method invocation in children to clear cached dependencies

    @NonNull
    public static DiImpl root(@NonNull String id, Module... modules) {
        return root(id, ArrayUtils.toList(modules));
    }

    @NonNull
    public static DiImpl root(@NonNull String id, @NonNull Collection<Module> modules) {
        return new DiImpl(
                InternalDependencies.create(),
                null,
                id,
                CollectionUtils.requireNoNulls(
                        "Cannot add `null` as a module",
                        modules
                ));
    }

    // this#dispose/close/exit

    private final InternalDependencies dependencies;
    private final DiImpl parent;
    private final String id;

    // if explicit dependency is obtained from parent we cannot supply it our context
    //       as parent must not know about children and thus be created inside self
    private final Map<Key, Contributor> explicit;

    private final Set<Key> pendingInjections = Collections.synchronizedSet(new HashSet<Key>(3));

    private volatile boolean isClosed;

    DiImpl(
            @NonNull InternalDependencies dependencies,
            @Nullable DiImpl parent,
            @NonNull String id,
            @NonNull Collection<Module> modules) {
        this.dependencies = dependencies;
        this.parent = parent;
        this.id = id;
        this.explicit = dependencies.explicitBuilder().build(parent, modules);
    }

    @NonNull
    @Override
    public DiCloseable fork(@NonNull String id, Module... modules) {
        return fork(id, ArrayUtils.toList(modules));
    }

    @NonNull
    @Override
    public DiCloseable fork(@NonNull String id, @NonNull Collection<Module> modules) {

        checkState();

        return new DiImpl(
                dependencies,
                this,
                id,
                CollectionUtils.requireNoNulls("Cannot add `null` as a module", modules));
    }

    @NonNull
    @Override
    public DiCloseable inject(@NonNull Service who) {

        checkState();

        dependencies.serviceInjector().inject(this, who);

        return this;
    }

    @NonNull
    @Override
    public DiCloseable acceptCloseable(@NonNull Visitor<DiCloseable> visitor) {
        visitor.visit(this);
        return this;
    }

    @NonNull
    @Override
    public DiCloseable accept(@NonNull Visitor<Di> visitor) {
        visitor.visit(this);
        return this;
    }

    @NonNull
    public <T> T get(@NonNull Key key) {

        checkState();

        if (pendingInjections.contains(key)) {
            throw DiException.halt("%s, Recursive injection for the key: %s",
                    this, key);
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
                //noinspection unchecked
                return (T) contributor.contribute(impl);
            }

            checkState();

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
            isClosed = true;
            explicit.clear();
        }
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    private void checkState() {

        boolean isClosed = false;

        DiImpl impl = this;

        // what if we cache called instances and

        while (impl != null) {
            if (impl.isClosed) {
                isClosed = true;
                break;
            }
            impl = impl.parent;
        }

        if (isClosed) {
            throw DiException.halt("%s: is closed", path());
        }
    }
}
