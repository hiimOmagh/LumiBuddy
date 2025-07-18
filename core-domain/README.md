# Core Domain Module

## Overview
The `core-domain` module defines the pure Java domain layer. It contains model classes, repository interfaces and simple use cases that are independent of Android. Other modules depend on these abstractions.

## Key Components
- **Model package** – domain models such as `Plant`, `PlantCareProfile`, `PlantStage`, `Measurement`.
- **Repository interfaces** – e.g. `MeasurementRepository` for observing sensor data.
- **Use cases** – `GetCurrentLuxUseCase` and other small interactors.
- **Utilities** – helper classes like `AppExecutors` for thread management.

## Dependencies
This module has minimal dependencies (RxJava and Dagger) and is used by `core-data`, `core-infra` and feature modules.

Dependency injection relationships are illustrated in [docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).

