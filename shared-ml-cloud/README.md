# shared-ml-cloud

Provides interfaces for downloading machine learning models from the cloud.

## Interfaces

The core abstraction is `CloudModelProvider` which exposes:

```java
ByteBuffer downloadModel(Context context, String modelId);
```

`downloadModel` retrieves the model identified by `modelId` and returns its raw bytes.

## Sample implementation

`NoOpCloudModelProvider` offers a simple HTTP based implementation that caches
models in the app's cache directory:

```java
public class NoOpCloudModelProvider implements CloudModelProvider {
    @Override
    public ByteBuffer downloadModel(Context context, String modelId) {
        // See source for full implementation.
    }
}
```

A Firebase based implementation can also be created using
`FirebaseModelDownloader`:

```kotlin
class FirebaseCloudModelProvider : CloudModelProvider {
    override fun downloadModel(context: Context, modelId: String): ByteBuffer {
        val conditions = CustomModelDownloadConditions.Builder().build()
        val model = Tasks.await(
            FirebaseModelDownloader.getInstance()
                .getModel(modelId, DownloadType.LOCAL_MODEL, conditions)
        )
        val file = model.file!!
        FileInputStream(file).channel.use { channel ->
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
        }
    }
}
```

## Dependencies

Add the Firebase and HTTP client libraries to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-ml-modeldownloader")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}
```

See the [Firebase setup guide](../docs/firebase_setup.md) for configuration details.
