# Grow Schedule Feature

### Purpose

Shows an agenda of upcoming care tasks and triggers reminders.

### Entry points

- `HomeFragment` – displays today's tasks
- `AgendaFragment` – shows upcoming tasks

### Main classes

- `AgendaViewModel` – exposes a list of `DiaryEntry` items
- `GrowReminderWorker` – WorkManager job for notifications

### Dagger component

`GrowScheduleComponent` depends on `CoreComponent`, injects `HomeFragment`, and provides a
`HomeViewModelFactory`.

### Dependencies

- `core-data` for task repositories
- `core-infra` for Worker scheduling
- interacts with `feature-diary` to record completed tasks

### Integration

`HomeFragment` builds `GrowScheduleComponent` in `onAttach`. Navigation routes are defined in
`feature-growschedule/src/main/res/navigation/nav_graph.xml`.

For details on how `GrowScheduleComponent` interacts with other components, see
[docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).
