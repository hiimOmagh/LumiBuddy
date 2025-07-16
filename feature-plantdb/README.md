# Plant Database Feature

Contains screens for managing plant profiles and connecting them with diary entries from the diary
feature.

**Entry point:** `PlantListFragment` as described in the architecture docs.

## Dagger component

`PlantDbComponent` depends on `CoreComponent` and supplies a `PlantDbViewModelFactory`. Fragments
build the component on attach:

```java
CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
PlantDbComponent component = DaggerPlantDbComponent.factory().create(core);
viewModelFactory =component.

viewModelFactory();
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for the full graph.