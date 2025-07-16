# Grow Schedule Feature

Provides reminders and scheduling for plant care tasks.

## Dagger Setup

`GrowScheduleComponent` injects `HomeFragment` and exposes a
`HomeViewModelFactory`.

```java
CoreComponent core = ((CoreComponentProvider)
        context.getApplicationContext()).getCoreComponent();
GrowScheduleComponent component =
        DaggerGrowScheduleComponent.factory().create(core);
viewModelFactory =component.

viewModelFactory();
```

For the complete dependency graph see
[../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).

