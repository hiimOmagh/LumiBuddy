# Augmented Reality Feature

### Purpose

Experimental ARCore module that renders a heatmap of light intensity in real time.

### Entry points

- `ArEntryActivity` – initialises ARCore
- `ArHeatmapActivity` – overlay for measurement visualisation

### Main classes

- `ArComponent` – DI entry point
- `ARMeasureOverlay` – draws heatmap using camera frames
- `LightProbeManager` (planned)

### Dependencies

- `core-infra` for measurement services
- `feature-measurement` to obtain light data
- Google ARCore / Sceneform libraries

### Integration

Activities build `ArComponent` from the application's `CoreComponent`. Collected measurements may be
stored via the measurement feature or displayed alongside plant details.
