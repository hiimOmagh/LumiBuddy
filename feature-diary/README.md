# Diary Feature

### Purpose

Stores and displays plant care events such as watering or fertilising. Allows browsing a timeline of
actions for each plant.

### Entry points

- `PlantDiaryFragment` – default destination in `diary_nav_graph.xml`
- `DiaryEntryDialog` – dialog for creating new entries

### Main classes

- `DiaryViewModel` – exposes LiveData of diary records
- `TaskViewModel` – schedules upcoming tasks
- `DiaryRepository` – persists entries using `core-data`

### Dagger component

`DiaryComponent` depends on `CoreComponent` and injects the view models above.

### Dependencies

- `core-data` for Room entities
- `core-infra` for background sync
- `feature-plantdb` for linking diary entries to a plant

### Integration

Other features navigate to `PlantDiaryFragment` using the shared NavController. `DiaryComponent` is
created when the fragment attaches and obtains `CoreComponent` from the application.

For a complete picture of how `DiaryComponent` fits into the app's dependency graph, see
[docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).
