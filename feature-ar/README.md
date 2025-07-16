# Augmented Reality Feature

Experimental module for visualizing light intensity in AR.

**Entry point:** `ArEntryActivity`.

## Dagger component

`ArComponent` depends on `CoreComponent` and injects `ArEntryActivity`.

```java
CoreComponent core = ((CoreComponentProvider) getApplicationContext()).getCoreComponent();
DaggerArComponent.

factory().

create(core).

inject(this);
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for more
information.