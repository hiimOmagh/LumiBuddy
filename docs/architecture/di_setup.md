# DI Setup

This page describes how feature modules obtain their Dagger components from the
shared `CoreComponent`. `LumiBuddyApplication` creates the `CoreComponent` on
startup and exposes it through `CoreComponentProvider` so that fragments or
view models in feature modules can build their own components when needed.

## Getting `CoreComponent`

The application implements `CoreComponentProvider`:

```java
public class LumiBuddyApplication extends Application
        implements CoreComponentProvider {
    private CoreComponent coreComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        coreComponent = DaggerCoreComponent.builder()
                .application(this)
                .build();
    }

    @Override
    public CoreComponent getCoreComponent() {
        return coreComponent;
    }
}
```

Fragments or view models cast their `Context` or `Application` to
`CoreComponentProvider` to access the instance.

## Creating feature components

Each feature module defines its own component with a `Factory` that accepts the
`CoreComponent`. The host fragment (or view model) builds the component during
`onAttach` (or construction) and keeps a reference while the screen is active.

### `GrowScheduleComponent`

Example from `HomeFragment` in `feature-growschedule`:

```java

@Override
public void onAttach(Context context) {
    super.onAttach(context);
    CoreComponent core = ((CoreComponentProvider)
            context.getApplicationContext()).getCoreComponent();
    component = DaggerGrowScheduleComponent.factory().create(core);
    viewModelFactory = component.viewModelFactory();
}
```

### Other modules

- **Measurement** – `MeasureFragment` creates
  `MeasurementComponent` using the same pattern and then injects itself.
- **Plant Database** – fragments like `PlantListFragment` build
  `PlantDbComponent` to obtain the view model factory.
- **Diary** – `DiaryViewModel` constructs `DiaryComponent` in its constructor
  to access the repository.

After obtaining a feature component, dependencies such as view model factories
are retrieved or injection methods are called. When the fragment is destroyed,
the reference can be dropped so the component is eligible for GC.
