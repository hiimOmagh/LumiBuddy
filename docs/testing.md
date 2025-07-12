# Testing Patterns

This project uses JUnit4 with Mockito for unit tests. Each module
contains tests under `src/test/java` that rely on mocked dependencies
so that repository and ViewModel logic can be verified in isolation.

Common steps:

1. Use `@Mock` and `MockitoAnnotations.openMocks(this)` to initialise
   mocks for DAOs or managers.
2. Provide `AppExecutors` or other required collaborators via mocks so
   background tasks run on a single thread executor during tests.
3. Call repository or ViewModel methods and then use `Mockito.verify`
   to assert that the appropriate DAO method was invoked.
4. When LiveData is returned, an observer can be attached with
   `observeForever` so the value is emitted synchronously.

Run all unit tests with:

```bash
./gradlew test
```

## AR Overlay Integration Testing

QA can validate the AR overlay on supported hardware using these steps:

1. Launch `ArEntryActivity` from the debug app menu.
2. Grant the camera permission when prompted.
3. Move the device until ARCore detects a plane and tap to place a marker.
4. Verify that a 3D text marker appears showing the current lux value.
5. Confirm the background color of the marker shifts from blue (low) to red (high) as light
   intensity changes.

The feature requires Google Play Services for AR and a physical device; emulators are not supported.
