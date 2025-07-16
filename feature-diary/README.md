# Diary Feature

This module tracks plant care tasks and history.

## Dagger Setup

`DiaryComponent` depends on `CoreComponent` and injects view models
such as `DiaryViewModel` and `TaskViewModel`.

```java
CoreComponent core = ((CoreComponentProvider) application).getCoreComponent();
DiaryComponent component =
        DaggerDiaryComponent.factory().create(core);
component.

inject(this);
```

The overall dependency graph is shown in
[../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).

