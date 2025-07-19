# Measurement Feature

### Purpose
Captures live lux readings using the light sensor or camera and converts them into PPFD and DLI metrics.

### Entry points
- `MeasureFragment`
- `CalibrationFragment` (planned)

### Main classes
- `MeasurementViewModel`
- `CameraLightMeter`
- `MeasurementEngine` from `core-infra`

### Dagger component
`MeasurementComponent` depends on `CoreComponent` and injects `MeasureFragment`.

### Dependencies
- `core-data` to store results
- `core-infra` for sensor access
- `shared-ml` for optional ML analysis

### Integration
Measurement results can be attached to diary entries or used by the grow schedule feature to verify light requirements.
