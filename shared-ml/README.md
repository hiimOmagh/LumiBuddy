# Shared ML Module

This module contains lightweight machine learning helpers used across the app.

## Plant Identifier

`PlantIdentifier` wraps a small TensorFlow Lite model to classify plant photos.
The model is supplied by a `ModelProvider` which abstracts where the
`ByteBuffer` comes from. By default the app uses `AssetModelProvider` to load
`MlConfig.PLANT_MODEL` from the module's assets.

### Basic Usage

```java
ModelProvider provider = new AssetModelProvider(MlConfig.PLANT_MODEL);
PlantIdentifier identifier = new PlantIdentifier(context, provider);
LiveData<java.util.List<PlantIdentifier.Prediction>> result = identifier.identifyPlant(bitmap);
```

Each instance creates its own single-threaded executor. You may also supply
a custom `ExecutorService` if needed and are then responsible for shutting it
down once the identifier is no longer used.

Any other `ModelProvider` implementation can be supplied, e.g. one that
retrieves a model from disk or downloads it.

## Lamp Identifier

`LampIdentifier` mirrors `PlantIdentifier` but targets grow lights. It relies on a small on-device
TensorFlow Lite model, loaded via a `ModelProvider`. The default provider loads
`MlConfig.LAMP_MODEL` from the module assets.

### Basic Usage

```java
ModelProvider provider = new AssetModelProvider(MlConfig.LAMP_MODEL);
LampIdentifier identifier = new LampIdentifier(context, provider);
LiveData<java.util.List<LampIdentifier.Prediction>> result = identifier.identifyLamp(bitmap);
```

Like `PlantIdentifier`, a dedicated executor is created per instance unless a
custom one is provided.

Results return a label only when the confidence exceeds the built‑in threshold. A cloud fallback
service could be queried when the label is `null` to improve accuracy.