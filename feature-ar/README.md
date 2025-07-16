# Augmented Reality Feature

Experimental AR overlay for visualizing light intensity.

## Dagger Setup

`ArComponent` injects `ArEntryActivity` and depends on `CoreComponent`.

```java
CoreComponent core = ((CoreComponentProvider) getApplicationContext()).getCoreComponent();
DaggerArComponent.

factory().

create(core).

inject(this);
```

See the [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md)
for context.
