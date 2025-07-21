# Plant Database Feature

### Purpose

Handles CRUD operations for plant profiles and exposes them to other modules.

### Entry points

- `PlantListFragment`
- `PlantDetailFragment`
- `AddPlantFragment`

### Main classes

- `PlantDbViewModel`
- `PlantRepository` from `core-data`
- `PlantDetailAdapter`

### Dagger component

`PlantDbComponent` depends on `CoreComponent` and exposes a `PlantDbViewModelFactory`.

### Dependencies

- `core-data` for Room access
- `core-infra` for sync scheduling
- links to `feature-diary` for care history

### Integration

Fragments obtain `PlantDbComponent` from the application's `CoreComponent`. Plant IDs are passed via
the NavController to other features such as the diary and measurement screens.
