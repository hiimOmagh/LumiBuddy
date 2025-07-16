# Plant Database Feature

Screens and logic for managing plant profiles and their diary entries.
This module depends on `feature-diary` for cross-linking events.

## Dagger Setup

`PlantDbComponent` is built from `CoreComponent` and supplies a
`PlantDbViewModelFactory`.

```java
CoreComponent core = ((CoreComponentProvider)
        context.getApplicationContext()).getCoreComponent();
PlantDbComponent component =
        DaggerPlantDbComponent.factory().create(core);
viewModelFactory =component.

viewModelFactory();
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md)
for all feature components.

