# Augmented Reality Feature

Experimental module for visualizing light intensity in AR.

**Entry point:** `ArEntryActivity`.

Additional demo overlay: `ArHeatmapActivity` shows a heatmap of
live light measurements using `ARMeasureOverlay`.

## Dagger component

`ArComponent` depends on `CoreComponent` and injects `ArEntryActivity`
and `ArHeatmapActivity`.

```java
CoreComponent core = ((CoreComponentProvider) getApplicationContext()).getCoreComponent();
DaggerArComponent.

factory().

create(core).

inject(this);
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for more
information.