# Class Overview

This page briefly describes several helper classes and Dagger pieces used throughout LumiBuddy.

## `OnSwipeTouchListener`

Located in `core-infra` under `util`, this listener detects vertical swipe gestures.
Fragments such as `MeasureFragment` attach it to a view to respond to swipe up or down events.
Override `onSwipeTop()` or `onSwipeBottom()` to react when the user swipes.

## `FeatureScope`

A custom `@Scope` annotation found in `core-infra/di`. Apply this to feature components so
objects provided within the component live as long as the feature (often a fragment or screen) is active.

## `NetworkModule`

Part of `core-infra`'s Dagger setup. `NetworkModule` supplies a singleton `Retrofit` instance
based on `Config.BASE_URL`. Other modules inject this client for REST API calls.

## Repositories & DAOs

Module `core-data` defines Room DAO interfaces and their associated repository classes.
DAOs (`PlantDao`, `DiaryDao`, `TaskDao`, etc.) perform database operations while repositories
like `PlantRepository` or `TaskRepository` map entities to domain models and run queries on
background executors. Feature modules depend on these repositories to read and update
application data.
