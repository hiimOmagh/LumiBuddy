# Test Coverage Review

This document summarizes the current state of LumiBuddy's automated tests and highlights areas
lacking coverage.

## Existing Unit Tests

- **core-infra** – covers sync logic (`DiarySyncManagerTest`, `PlantSyncManagerTest`,
  `SyncSchedulerTest`).
- **feature-measurement** – tests for `MeasureViewModel` and `LampProfilesViewModel` plus repository
  tests.
- **feature-plantdb** – extensive ViewModel tests (`AddPlantViewModel`, `PlantDetailViewModel`,
  etc.).
- **feature-growschedule** – `HomeViewModelTest` and `AgendaViewModelTest`.
- **feature-diary** – repository and `DiaryViewModel` tests.
- **app** – high level `HomeViewModelTest` and some integration tests.

Most modules include the default `ExampleUnitTest`, but the above represent meaningful coverage.

## Existing Instrumentation Tests

Instrumentation tests are limited. The app module exercises navigation and a few manager classes (
e.g. `CalibrationManagerTest`, `WateringSchedulerTest`). Other modules only contain placeholder
`ExampleInstrumentedTest` files.

## ViewModels Without Tests

- `TaskViewModel` (feature-diary)

## Fragments Without Tests

All UI Fragments lack dedicated tests. This includes:

- `HomeFragment` and `AgendaFragment` (feature-growschedule)
- `PlantDiaryFragment`, `PlantTaskFragment`, and `PlantGrowthTimelineFragment` (feature-diary)
- `AddPlantFragment`, `PlantListFragment`, `PlantDetailFragment`, `ProfileFragment` (
  feature-plantdb)
- `MeasureFragment`, `LampProfilesFragment`, `CalibrationFragment`, `SettingsFragment`,
  `CalibrationWizardFragment` (feature-measurement)

## Sample Tests

Two new sample tests demonstrate how to exercise measurement and sync components:

- `MeasurementEngineTest` verifies that `MeasurementEngine` delegates to `LightSensorProvider`.
- `GrowLightSyncManagerTest` uses reflection to inject a mock `GrowLightApiService` and asserts that
  profiles are inserted into the DAO.

## Checklist Toward Full Coverage

- [ ] Unit tests for every ViewModel, including `TaskViewModel`.
- [ ] Tests for repository classes not yet covered.
- [ ] Instrumentation tests for each Fragment to verify basic rendering and navigation.
- [ ] Integration tests for network sync classes using a `MockWebServer`.
- [ ] Continuous integration workflow that runs both unit and instrumentation tests.

## Continuous Integration

GitHub Actions workflows already run `./gradlew test` on pull requests. To execute instrumentation
tests automatically, extend the workflow with the Android emulator container and run:

```yaml
- name: Run instrumentation tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 30
    script: ./gradlew connectedCheck
```

This will ensure UI tests execute on every commit.
