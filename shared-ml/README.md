# Shared ML Module

This module contains lightweight machine learning helpers used across the app.

## Plant Identifier

`PlantIdentifier` wraps a small TensorFlow Lite model to classify plant photos.
The model is supplied by a `ModelProvider` which abstracts where the
`ByteBuffer` comes from. By default the app uses `AssetModelProvider` to load
`plant_identifier.tflite` from the module's assets.

### Basic Usage

```java
ModelProvider provider = new AssetModelProvider("plant_identifier.tflite");
PlantIdentifier identifier = new PlantIdentifier(context, provider, executors);
LiveData<java.util.List<PlantIdentifier.Prediction>> result = identifier.identifyPlant(bitmap);
```

Any other `ModelProvider` implementation can be supplied, e.g. one that
retrieves a model from disk or downloads it.

## Lamp Identifier

`LampIdentifier` mirrors `PlantIdentifier` but targets grow lights. It relies on a small on-device
TensorFlow Lite model, loaded via a `ModelProvider`. The default provider loads
`lamp_identifier.tflite` from the module assets.

### Basic Usage

```java
ModelProvider provider = new AssetModelProvider("lamp_identifier.tflite");
LampIdentifier identifier = new LampIdentifier(context, provider, executors);
LiveData<java.util.List<LampIdentifier.Prediction>> result = identifier.identifyLamp(bitmap);
```

Results return a label only when the confidence exceeds the built‑in threshold. A cloud fallback
service could be queried when the label is `null` to improve accuracy.