# Dagger Component Relationships

The application uses a modular Dagger 2 setup. Components are arranged in a parent–child style so
feature modules can obtain dependencies from the core layer.

![Dagger Graph](../diagrams/dagger_components.png)

## CoreComponent

Built in `core-infra`. Provides singletons such as:

- `AppDatabase`
- repository instances
- settings and user profile managers
- executors and Firebase helpers

`CoreComponent` is created in `LumiBuddyApplication` using the Android `Application` instance.

## AppComponent

Lives in the `:app` module and depends on `CoreComponent`. It injects the `Application` and
`MainActivity`. This component is annotated with `@Singleton` and built at application start.

## Feature Components

Each feature module defines its own component that also depends on `CoreComponent`:

- **MeasurementComponent** – injects `MeasureFragment` and provides a `MeasureViewModelFactory`.
- **PlantDbComponent** – provides `PlantDbViewModelFactory` for plant database screens.
- **DiaryComponent** – injects `DiaryViewModel` and related classes.
- **GrowScheduleComponent** – injects `HomeFragment` and provides a `HomeViewModelFactory`.
- **ArComponent** – injects `ArEntryActivity` for AR features.

Components share the same `CoreComponent` instance, so repositories and managers are singletons
across the app. Feature components are usually created by their host fragments when needed and
destroyed afterwards.

Diagram source: [docs/diagrams/dagger_components.dot](../diagrams/dagger_components.dot)