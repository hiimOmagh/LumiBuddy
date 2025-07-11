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
LiveData<String> result = identifier.identifyPlant(bitmap);
```

Any other `ModelProvider` implementation can be supplied, e.g. one that
retrieves a model from disk or downloads it.
