# Core Data Module

## Overview
`core-data` provides the application's data layer. It contains the Room database, DAO interfaces and repository implementations used by feature modules. This module converts database entities to domain models defined in `core-domain` and exposes `LiveData` streams.

## Key Components
- **`AppDatabase`** – Singleton Room database configuring all entities and migrations.
- **DAO classes** – `PlantDao`, `DiaryDao`, `GrowLightDao`, `PlantSpeciesDao`, `TaskDao`, `LightCorrectionDao` and others.
- **Repositories** – `PlantRepository`, `DiaryRepository`, `TaskRepository`, `LightCorrectionRepository` and Firebase backed repositories under `repository.firebase`.
- **Model classes** – Entity representations such as `Plant`, `DiaryEntry`, `GrowLightProfile`, `Task`.

## Dependencies
This module depends on `core-domain` for the domain models and utilities. It also pulls in Room, LiveData and Firebase Firestore libraries.

See [docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for how repositories are provided through Dagger.

