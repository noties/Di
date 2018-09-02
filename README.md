# Di
[![di](https://img.shields.io/maven-central/v/ru.noties/di.svg?label=di)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22di%22)
[![di-android](https://img.shields.io/maven-central/v/ru.noties/di-android.svg?label=di-android)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22di-android%22)

Small [Dependency Injection](#) library for **quick prototyping** and **fast builds**
(reflection-based, no annotation processing). Everything is inside a scope. Scopes are organized
in a tree structure with ability to be closed. No dependency can out-live its scope. Even singleton.

```groovy
implementation 'ru.noties:di:0.7.0'
implementation 'ru.noties:di-android:0.7.0'
```

## Key points
* everything lies within a scope
* scopes are structured in a tree
* scopes are closable
* closing a scope closes all of its children
* scopes hold bindings (provided dependencies)
* 2 kinds of bindings:
  * _explicit_, defined via code in a `Module`
  * _implicit_, automatically created by the library
* no binding can outlive scope (even a singleton binding)
* implicit binding always stored inside a scope that requested it and is not shared with siblings
* child can request explicit binding from a parent (up to the root)
* no parent can access children's bindings
* child can override parent's explicit bindings
* no thermosiphon
* nor thermosiphon2

## Quick start

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // this method returns an instance, so we actually can
        // store it if we would require so additional handling
        // final Di di = Di.root(...).accept(...);
        Di.root(configuration, "App", new AppModule(this))
            .accept(ActivityInjector.init(this));
    }
}
```

Create a _root_ instance of `Di`. Please note that `#root` is a factory method that returns a _new_ root instance each time it is called. There is no static cache. Then we want to provide this root instance to our siblings. We are using `ActivityInjector` class from `di-android` module.

```java
public class MainActivity extends FragmentActivity implements Di.Service {
    
    @Inject
    private App app;

    @Override
    public void init(@NonNull Di di) {
        di
                .inject(this)
                .accept(FragmentInjector.init(getSupportFragmentManager()));
    }
}
```

A class can call `di.inject(this)` only if it implements `Di.Service`.

```java
public interface Service {
    void init(@NonNull Di di);
}
```

When creating a root instance one can specify _modules_ that provide _bindings_.

```java
public class AppModule extends Module {

    private final App app;

    AppModule(@NonNull App app) {
        this.app = app;
    }

    @Override
    public void configure() {
        bind(Application.class).with(() -> app).asSingleton();
    }
}
```

### Bindings

#### Explicit

_Explicit_ binding is a binding that is explicitly stated in a _module_.

* `bind(MyClass.class)` -&gt; simply bind concrete implementation of a class. It will be constructed by the library. It **have to** have an empty constructor with `@Inject` annotation
* `bind(CharSequence.class).as(String.class)` -&gt; provides String when CharSequence is requested. Class will be constructed by the library
* `bind(CharSequence.class).with(Provider<CharSequence>)` -&gt; when CharSequence is requested specified Provider will be called to return an instance

Bindings can have `@Named` and custom `@Qualifier` annotations

* `bind(MyClass.class).named("my-name")`
* `bind(MyClass.class).qualifier(MyQualifier.class)` (there is no support for custom `@Qualifier` annotation with parameters)

Bindings can have additional modifiers:

* `bind(MyClass.class).asSingleton()` -&gt; will have this binding a singleton inside requested scope (and its children)
* `bind(MyClass.class).asLazy()` -&gt; will bind `Lazy<MyClass>`
* `bind(MyClass.class).asProvider()` -&gt; will bind `Provider<MyClass>`

These modifiers can be combined:
```java
// Lazy<Provider<MyClass>>
bind(MyClass.class)
    .asLazy()
    .asProvider()
    .asSingleton();
```

Alongside with concrete implementations one can bind generic types:

```java
bind(new TypeToken<List<String>>(){}.getType())
    .with(() -> Arrays.asList("one", "two", "three"));
```

#### Implicit

Implicit binding is constructed by the library. Implicit binding cannot specify what scope it operates, nor specify modifiers (like `asSingleton`). It will always be created in a scope that it is requested. And each time this binding is requested a new instance will be returned. To change that consider using _explicit_ binding.

The only requirement for _implicit_ binding is to have an empty constructor annotated with `@Inject` annotation.

```java
public class MyClass implements OnInjected {

    @Inject
    private Action action;

    @Inject
    public MyClass() {
    }

    @Override
    public void onInjected() {
        action.apply();
    }
}
```

This binding can request own dependencies which will be provided by the scope that this binding is operating:

```java
public interface Action {
    void apply();
}
```

And two implementations:

```java
public class ActionNoOp implements Action {

    @Inject
    public ActionNoOp() {
    }

    @Override
    public void apply() {

    }
}

public class ActionOp implements Action {

    @Inject
    public ActionOp() {
    }

    @Override
    public void apply() {
        throw new RuntimeException();
    }
}
```

```java
public class Root implements Di.Service {

    public static void main(String[] args) {
        final Root root = new Root();
        Di
                .root("Root", new Module() {
                    @Override
                    public void configure() {
                        bind(Action.class).as(ActionNoOp.class);
                    }
                })
                .accept(root::init);
    }

    // here it will be `MyClass{ActionNoOp}`
    @Inject
    private MyClass myClass;

    @Override
    public void init(@NonNull Di di) {
        final Sibling sibling = new Sibling();
        di
                .inject(this)
                .accept(sibling::init);

    }
}
```

```java
public class Sibling implements Di.Service {

    // here it will be `MyClass{ActionOp}`
    @Inject
    private MyClass myClass;

    @Override
    public void init(@NonNull Di di) {
        di
                .fork("Sibling", new Module() {
                    @Override
                    public void configure() {
                        bind(Action.class).as(ActionOp.class);
                    }
                })
                .inject(this);
    }
}
```

## Scopes

Scopes are organized in a tree structure. To create a scope one call `di#fork` on a `Di` instance that will become the parent for newly created one.

After a _fork_ is created it can be closed (clearing internal cache, all bindings) which will also close all of its children.

```
  Copyright 2018 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```