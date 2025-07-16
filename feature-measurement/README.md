# Measurement Feature

This module displays live light readings and estimated PPFD/DLI values.

## Dagger Setup

`MeasurementComponent` depends on `CoreComponent` and injects `MeasureFragment`.
Fragments build the component in `onAttach`:

```java
CoreComponent core = ((CoreComponentProvider)
        context.getApplicationContext()).getCoreComponent();
MeasurementComponent component =
        DaggerMeasurementComponent.factory().create(core);
component.

inject(this);
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for the overall
component diagram.

