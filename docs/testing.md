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