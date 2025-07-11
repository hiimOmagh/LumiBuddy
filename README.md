# LumiBuddy 🌱

![CI](https://github.com/hiimOmagh/LumiBuddy/actions/workflows/ci.yml/badge.svg)
**Plant Diary & Light Measurement App**  
An Android app for hobbyists, growers, and researchers to monitor plant growth, light exposure, and
care events using smartphone sensors, camera, AR, and ML technologies.

---

## 🔭 Project Vision

**LumiBuddy aims to be the smartest mobile companion for plant lovers**—enabling plant monitoring
through environmental sensing, light spectrum measurement, AI-assisted recommendations, and
community interaction.

---

## 📐 Architecture Overview

### Clean Hexagonal Architecture with Modular Design

```
LumiBuddy/
├── :app                   // Entry point, Dagger, navigation, theming
├── :core-domain           // Entities and use cases (Java only)
├── :core-data             // Room DB, repositories, mappers
├── :core-infra            // Android-specific: SensorManager, Camera, DI
├── :feature-measurement  // Live lux, PPFD, DLI reading UI
├── :feature-plantdb      // Plant profile management
├── :feature-diary        // Timeline of care events
├── :feature-growschedule // Reminder engine (WIP)
```

### Planned Modules

- `:feature-ar` – ARCore features (WIP)
- `:shared-sensor` – Light, camera, external BLE sensor abstraction
- `:shared-ml` – On-device TFLite inference

For more details on fragment routes and dependency injection see
[docs/architecture/navigation_overview.md](docs/architecture/navigation_overview.md), [docs/architecture/dagger_graph.md](docs/architecture/dagger_graph.md),
and [docs/architecture/di_setup.md](docs/architecture/di_setup.md).

---

## 📦 Core Technologies

| Purpose            | Tech Stack                                 |
|--------------------|--------------------------------------------|
| Language           | Java (Kotlin DSL for Gradle only)          |
| Persistence        | Room, SharedPreferences, SQLCipher         |
| DI Framework       | Dagger 2 (modular components)              |
| UI/UX              | XML layouts + MVVM + ViewModel             |
| Sensor Integration | SensorManager, CameraX, custom calibration |
| AR Support         | ARCore, Sceneform (planned)                |
| AI/ML              | TFLite + On-device inference pipeline      |
| Networking         | Retrofit + Moshi                           |
| Charting           | MPAndroidChart                             |
| Logging            | Timber, LeakCanary (debug only)            |
| Background Tasks   | WorkManager                                |
| Cloud              | Firebase (Auth, Firestore, Functions, FCM) |

---

## 🌞 Light Measurement System

### Metrics Supported

| Metric            | Meaning               | Derived via                     |
|-------------------|-----------------------|---------------------------------|
| **Lux**           | Human-eye light       | Ambient Light Sensor or Camera  |
| **PPFD**          | μmol/m²/s photon flux | Camera + diffuser + calibration |
| **DLI**           | mol/m²/day light dose | Integration of PPFD over time   |
| **CCT**           | Kelvin color temp     | RGB chromaticity from camera    |
| **Spectrum Type** | Light classification  | RGB ratio pattern matching      |

### Key Techniques Used

- **Camera-based PPFD approximation**:
  - Use of diffuser (matte white paper)
  - Exposure locking and color calibration
  - Spectrum-type-specific conversion factors (sunlight, blurple LED, HPS, etc.)

- **Lux-to-PPFD Conversion**:
  - Accurate mapping per light type (e.g., 1 lux = 0.0185 μmol/s for sunlight)
  - Dynamic factor adjustment for different LED spectrums

- **DLI Estimation**:
  - Manual logging or periodic sensor snapshots
  - Formula: `DLI = PPFD * light_hours * 3600 / 1e6`

- **Spectrum Heuristics**:
  - RGB ratios used to detect likely light source
  - Visualized via approximate bar chart (red/green/blue photon distribution)

---

## 📊 Core Features

### ✅ MVP Features (DONE or STABLE)

- 📷 **Live Lux/PPFD reading** (with camera and light sensor)
- 📖 **Plant Database**: name, species, care history
- 🪴 **Care Diary**: watering, fertilizing, pruning
- 📊 **Charts**: Light trend, DLI timeline, care events
- 📤 **CSV export/share**
- 🔔 **Reminder System** (via WorkManager)

### 🔮 In Progress / Next

- 🧠 **Plant Identifier** (ML on-device + fallback cloud)
- 🧪 **Disease Detection** (CNN, Grad-CAM overlays)
- 🌐 **Cloud Sync** (Firebase + Firestore)
- 📱 **AR Assistant** (light heatmap, growth simulation)
- 🎛️ **Grow Light Simulator** (distance/PPFD visualization)

---

## 🧩 Modular DI with Dagger 2

### Components

```java

@Singleton
@Component(modules = {NetworkModule.class, DataModule.class, SensorModule.class})
public interface CoreComponent {
  void inject(LumiBuddyApplication app);
}
```

```java

@Singleton
@Component(dependencies = CoreComponent.class)
public interface AppComponent {
  void inject(MeasureViewModel vm);

  void inject(PlantDetailViewModel vm);
}
```

---

## 🧪 Testing Strategy

| Layer       | Framework                                  | Coverage |
|-------------|--------------------------------------------|----------|
| Core Logic  | JUnit, Mockito                             | ✅        |
| DAOs        | Instrumentation                            | ✅        |
| ViewModels  | JUnit + LiveData TestRules                 | ✅        |
| UI          | Espresso, Compose Testing (where relevant) | 🚧       |
| Integration | Firebase Test Lab                          | 🚧       |
| Performance | Android Profiler, LeakCanary               | ✅        |

---

## 📄 Documentation Plan

**README Sections**

- ✅ Architecture & Structure
- ✅ Light Measurement Guide
- ✅ Current Feature Set
- ✅ Roadmap
- ✅ Dagger Setup
- ✅ Modular Guidelines

-**To Add**

- ✅ Developer Setup Instructions
- ✅ Emulator Profiles for Testing
- ✅ ARCore Device Compatibility
- ✅ CSV Export Format Spec
- ✅ Grow Light Spectrum Calibration Table

---

## 🛠️ Developer Setup

### Prerequisites

- **JDK 17** (Gradle uses AGP 8.10.1, modules target Java 11)
- **Android Studio Hedgehog (2023.1.1)** or newer

### local.properties

1. Copy `local.properties.example` to `local.properties` in the project root
2. Set your Android SDK path:
   ```
   sdk.dir=/path/to/Android/sdk
   ```

### Build & Run

- Build debug APK:
  ```
  ./gradlew assembleDebug
  ```
- Install on a connected device/emulator:
  ```
  ./gradlew installDebug
  ```
- Or open the project in Android Studio and press **Run**.
- ### Recommended Emulator Profiles

| Device Profile | API Level       | Notes                                |
|----------------|-----------------|--------------------------------------|
| Pixel 5        | 34 (Android 14) | Default Google Play image            |
| Pixel 4        | 30 (Android 11) | Good baseline for sensors and ARCore |
| 2GB Android Go | 28 (Android 9)  | Low-end performance test             |

### ARCore Device Compatibility

Most recent Pixel, Samsung Galaxy, and OnePlus devices support ARCore.
Check [Google's official list](https://developers.google.com/ar/devices) to verify a specific model.
On a device, ensure the **Google Play Services for AR** package is installed or run:

```bash
adb shell pm list packages | grep arcore
```

Emulators do not support ARCore-based features.

### CSV Export Format

CSV exports contain these columns in order:

| Column       | Unit       | Description                       |
|--------------|------------|-----------------------------------|
| `timestamp`  | ISO 8601   | Local date/time of entry          |
| `lux`        | lux        | Ambient light reading             |
| `ppfd`       | µmol/m²/s  | Photon flux density               |
| `dli`        | mol/m²/day | Daily light integral              |
| `event_type` | -          | Diary event type or `measurement` |

Example:

```csv
timestamp,lux,ppfd,dli,event_type
2024-03-10T14:00:00,15000,278,12.4,measurement
```

### Grow Light Spectrum Calibration Table

Default lux→PPFD conversion factors used by LumiBuddy:

| Spectrum       | Lux→PPFD Factor (µmol/m²/s per lux) |
|----------------|-------------------------------------|
| Sunlight       | 0.0185                              |
| Warm White LED | 0.017                               |
| Cool White LED | 0.020                               |
| Blurple LED    | 0.022                               |
| HPS            | 0.012                               |

## 📝 Developer To-Do List

### App Structure

- [x] Finalize modular structure
- [x] Validate Dagger modules + DI scopes
- [x] Improve documentation of feature entry points (Fragment routes, nav graph)

### Sensor & Measurement

- [x] CameraX integration with manual exposure
- [x] Lux to PPFD calculation with selectable spectrum
- [ ] Add calibration UI wizard for advanced users
- [ ] Store device correction factor per light type

### AR & ML

- [ ] Set up ARCore scene + light heatmap mesh
- [ ] Port TFLite plant ID model into :shared-ml
- [ ] Setup ML fallback to cloud model if confidence < threshold

### Cloud

- [ ] Enable Firebase Auth + Firestore integration
- [ ] Data backup & sync
- [ ] Setup model distribution pipeline (Cloud Function)

### UX / UI

- [x] Reorganize settings screen
- [x] Add CSV export timestamp suffix
- [x] Add share CSV feature
- [ ] Dark mode polish
- [ ] AR toggle on measurement screen

### Testing & CI

- [ ] Improve test coverage on ViewModels and Repos
- [ ] GitHub Actions for PR builds and unit test automation

---

## 🌍 License

License: MIT
