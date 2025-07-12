# Feature Entry Points

This document summarizes the starting `Fragment` or `Activity` for each feature module and where its
navigation graph is defined. It also notes which Dagger component injects that entry point.

| Module                 | Entry Fragment/Activity | Navigation Graph                                             | Injecting Dagger Component |
|------------------------|-------------------------|--------------------------------------------------------------|----------------------------|
| `feature-measurement`  | `MeasureFragment`       | `feature-measurement/src/main/res/navigation/nav_graph.xml`  | `MeasurementComponent`     |
| `feature-plantdb`      | `PlantListFragment`     | `feature-plantdb/src/main/res/navigation/nav_graph.xml`      | `PlantDbComponent`         |
| `feature-diary`        | `PlantDiaryFragment`    | `feature-diary/src/main/res/navigation/diary_nav_graph.xml`  | `DiaryComponent`           |
| `feature-growschedule` | `HomeFragment`          | `feature-growschedule/src/main/res/navigation/nav_graph.xml` | `GrowScheduleComponent`    |
| `feature-ar`           | `ArEntryActivity`       | (none yet)                                                   | (none)                     |

