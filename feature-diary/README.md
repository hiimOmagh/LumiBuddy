# Diary Feature

Tracks plant care tasks and history.

**Entry point:** `PlantDiaryFragment`.

## Dagger component

`DiaryComponent` depends on `CoreComponent` and injects `DiaryViewModel` and `TaskViewModel`.
Fragments create the component when their view models are constructed:

```java
CoreComponent core = ((CoreComponentProvider) application).getCoreComponent();
DiaryComponent component = DaggerDiaryComponent.factory().create(core);
component.

inject(this);
```

More details are shown
in [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).