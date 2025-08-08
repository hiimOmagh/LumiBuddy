# ARCore Abstractions

LumiBuddy isolates ARCore details behind small interfaces so feature modules can opt into AR
features without tightly coupling to the ARCore SDK.

## Shared session provider

The `shared-arcore` module exposes the `ArCoreSessionProvider` interface. Implementations such as
`DefaultArCoreSessionProvider` and `NoOpArCoreSessionProvider` create or stub out
`com.google.ar.core.Session` instances depending on device support.

## Growth tracking interface

`core-infra` defines an `ARGrowthTracker` interface for placing visual markers and tracking plant
growth. `ARCoreGrowthTracker` supplies a full ARCore-based implementation, while
`DummyARGrowthTracker` acts as a lightweight stub.

## Feature consumption

- `feature-ar` builds an `ARCoreGrowthTracker` to record light measurement markers in
  `ArEntryActivity`.
- `feature-plantdb` uses `DummyARGrowthTracker` to optionally preview growth overlays when adding a
  plant.

## Dependency graph

![ARCore dependencies](../diagrams/arcore_dependencies.png)

For how these pieces are wired into the application components, see
the [Dagger component relationships](dagger_graph.md).