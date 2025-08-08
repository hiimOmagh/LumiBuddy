# Measurement Feature

### Purpose

Captures live lux readings using the light sensor or camera and converts them into PPFD and DLI
metrics.

### Entry points

- `MeasureFragment`
- `CalibrationFragment`
- `LampProfilesFragment`

### Main classes

- `MeasurementViewModel`
- `CameraLightMeter`
- `MeasurementEngine` from `core-infra`

### Dagger component

`MeasurementComponent` depends on `CoreComponent`, injects `MeasureFragment` and
`LampProfilesFragment`, and provides the corresponding ViewModel factories.

### Dependencies

- `core-data` to store results
- `core-infra` for sensor access
- `shared-ml` for optional ML analysis

### Integration

Measurement results can be attached to diary entries or used by the grow schedule feature to verify
light requirements.

For a diagram of how `MeasurementComponent` connects to other components, see
[docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).
