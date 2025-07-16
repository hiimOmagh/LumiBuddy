# README Update Notes

To keep documentation consistent across modules the following README changes are required:

- **Project README** now links to [`docs/architecture/module_map.md`](module_map.md) and notes the
  `docs/diagrams` folder containing the source Graphviz files.
- Each feature module should have its own README describing its purpose and Dagger component:
    - `feature-measurement` – document `MeasurementComponent` creation and injection points.
    - `feature-plantdb` – document `PlantDbComponent` and dependency on `feature-diary`.
    - `feature-diary` – document `DiaryComponent`.
    - `feature-growschedule` – add a README covering `GrowScheduleComponent`.
    - `feature-ar` – add a README explaining `ArComponent` and AR entry activity.

These individual READMEs should link back to the main [Dagger graph](dagger_graph.md) for context.

