# Grow Schedule Feature

Provides reminders and scheduling for plant care tasks.

**Entry point:** `HomeFragment`.

## Dagger component

`GrowScheduleComponent` depends on `CoreComponent` and exposes a `HomeViewModelFactory` while
injecting `HomeFragment`.

```java
CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
GrowScheduleComponent component = DaggerGrowScheduleComponent.factory().create(core);
viewModelFactory =component.

viewModelFactory();
```

See [../docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for how this fits
into the overall graph.