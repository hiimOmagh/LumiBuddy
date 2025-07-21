# Core Infra Module

## Overview

`core-infra` provides Android specific infrastructure and implementations of domain interfaces. It
wires together sensors, camera utilities, Firebase helpers and background synchronization tasks. The
module also defines the central Dagger `CoreComponent` used by the application and feature modules.

## Key Components

- **Dependency injection** – `CoreComponent` and modules such as `NetworkModule`, `DataModule`,
  `SensorModule`, `ExecutorModule`, `UserModule` and `SyncModule`.
- **Measurement** – classes like `ALSManager`, `CameraLightMeter`, `MeasurementEngine` and
  `CameraSpectralProvider` for obtaining light data.
- **Sync** – `SyncScheduler`, `PlantSyncManager`, `DiarySyncManager` and associated `Worker`
  classes.
- **Firebase integration** – `FirebaseManager` for Auth and Firestore access.
- **User & settings** – `SettingsManager`, `UserProfileManager`, `CalibrationProfilesManager`.

## Dependencies

This module depends on both `core-domain` and `core-data` and is required by all feature modules. It
includes AndroidX libraries, CameraX, WorkManager and Firebase services.

Refer to [docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md) for how
`CoreComponent` fits into the overall dependency graph.

