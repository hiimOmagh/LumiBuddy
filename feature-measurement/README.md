# Measurement Feature

Responsible for displaying live light readings and converting them to PPFD/DLI values.

**Entry point:** `MeasureFragment` as noted in the table of feature entry points.

## Dagger component

`MeasurementComponent` lives in this module. It depends on `CoreComponent` and provides a
`MeasureViewModelFactory`. `MeasureFragment` builds the component in `onAttach`:

```java
CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
MeasurementComponent component = DaggerMeasurementComponent.factory().create(core);
component.

inject(this);
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for the complete
dependency diagram