# LumiBuddy – Android Plant Care & Light Measurement App

## Overview

LumiBuddy is a modular, scalable Android app designed for plant enthusiasts—from hobbyists to professional gardeners. It enables accurate environmental measurements, plant management, health monitoring, data visualization, and predictive analytics.

This document serves as a comprehensive guide, detailing the application's architecture, current features, modular structure, and future development guidelines. It acts as the authoritative reference for developers and tools (including Codex/GPT) to ensure alignment with the project's vision and architecture.

---

## Core Vision

**Provide users with a seamless and intuitive experience to manage plants by accurately measuring, logging, analyzing environmental conditions, and offering insightful recommendations.**

### Primary Features

* **Real-time Sensor Measurements**: Lux, PPFD, DLI, temperature, humidity.
* **Comprehensive Plant Database**: Manage plant profiles, care schedules, and notes.
* **Care Diary**: Track watering, fertilizing, pruning, and other care events.
* **Measurement Analytics & Charts**: Data visualization for trends and insights.
* **Image Handling**: Attach and manage plant photos.
* **Data Export**: CSV export for external analysis or backup.
* **Modular and Extensible Architecture**: Ready for future AR, ML, and cloud integration.

---

## Architecture & Modular Structure

LumiBuddy strictly follows a **Clean/Hexagonal Architecture** approach, designed for:

* **Clear separation of concerns**.
* **Independent module testability**.
* **Scalable and parallel development**.

### Layers

* **Domain Layer (`:core-domain`)**:

  * Pure Java models and business use cases.
  * Absolutely no Android or Room dependencies.

* **Data Layer (`:core-data`)**:

  * Room database entities, DAOs and repositories.
  * Depends only on `:core-domain`.

* **Infrastructure Layer (`:core-infra`)**:

  * Android implementations of domain interfaces (sensors, networking).
  * Provides Dagger modules and other platform services.

* **Presentation Layer (`:feature-*`)**:

  * UI components, Activities, Fragments, ViewModels, layouts, resources.

* **Application Module (`:app`)**:

  * Application entry point, initialization of core services (Dagger, LeakCanary, Timber).
  * Main navigation graph and high-level integration.

---

## Current Modular Structure

```
LumiBuddy/
├── :app                         // Main application module
├── :core-domain                 // Entities, business logic
├── :core-data                   // Room DB, repositories
├── :core-infra                  // Infrastructure, services, DI modules
├── :feature-measurement         // Light/environment measurement UI
├── :feature-plantdb             // Plant database management
├── :feature-diary               // Care diary management
├── :feature-growschedule        // Grow schedule & reminders (future module)
├── :feature-ar                  // AR integration placeholder (future module)
├── gradle/libs.versions.toml    // Shared dependency version catalog
├── settings.gradle.kts          // Gradle module settings
└── README.md                    // Project documentation
```

Module dependencies:

- `:core-data` depends on `:core-domain`.
- `:core-infra` depends on `:core-domain` and Android libraries.
- `:app` pulls in all three cores and each feature module.

---

## Technology Stack

### Core Technologies (Java & XML-based)

* **AndroidX Libraries**
* **Room Database**: Local data persistence
* **Retrofit**: Network communication
* **Dagger 2**: Dependency Injection (Java)
* **RxJava & RxAndroid**: Reactive programming for async streams
* **Glide**: Image loading and caching
* **MPAndroidChart**: Data visualization
* **Timber**: Structured logging
* **LeakCanary**: Memory leak detection (debug builds)

---

## DI Setup

LumiBuddy uses **Dagger 2** with two main components.

- **CoreComponent** (in `:core-infra`) provides network, database and sensor services via
  `NetworkModule`, `DataModule` and `SensorModule`.
- **AppComponent** (in `:app`) depends on `CoreComponent` to wire feature modules.

Example:

```java

@Singleton
@Component(modules = {NetworkModule.class, DataModule.class, SensorModule.class})
public interface CoreComponent {
  void inject(LumiBuddyApplication app);

  void inject(MeasureViewModel viewModel);
}

@Component(dependencies = CoreComponent.class)
public interface AppComponent {
  void inject(HomeActivity activity);
}
```

`LumiBuddyApplication` initializes `coreComponent = DaggerCoreComponent.create()` in `onCreate` and
exposes it to ViewModels.

---

## Development Guidelines & Best Practices

### Modularization

* Each module handles clearly defined responsibilities.
* Feature modules encapsulate their specific UI and logic.
* Common logic resides in core modules.

### Dependency Injection

* All dependencies provided via Dagger 2.
* Modules clearly define provided services, repositories, and infrastructure components.

### UI Development

* XML layouts and Android Views.
* MVVM pattern with ViewModels managing UI state.

### Reactive Programming

* Use RxJava for handling sensor streams, network calls, and database interactions asynchronously.

### Logging & Debugging

* Timber for logging.
* LeakCanary to proactively identify memory leaks.

### Code Quality

* Write unit tests for core logic (JUnit, Mockito).
* Regularly conduct manual and automated UI tests.
* Ensure all code is modular, well-documented, and easy to maintain.

---

## Planned Improvements & Roadmap

### Structural & Immediate Improvements

* **Complete modularization**: Move remaining logic and data management fully into domain, data, and infra modules.
* **Enhanced DI**: Expand Dagger components for finer-grained dependency management.
* **Increased Test Coverage**: Expand unit tests, introduce instrumentation tests.

### Feature Enhancements (Near-term)

* **Advanced Analytics**: Predictive plant health analytics using aggregated sensor data.
* **Improved UI/UX**: Polished and intuitive UI enhancements.
* **Cloud Sync & Backup**: Firebase/Google Cloud integration for data synchronization and backup.
* **Export & Import Enhancements**: More formats and user-friendly exports.

### Future Expansion (Long-term)

* **Augmented Reality (AR)**: ARCore integration for visualization and educational overlays.
* **Machine Learning (ML)**: Plant identification, disease detection.
* **Community & Social Integration**: Sharing insights, plant profiles, and recommendations.
* **Smart Home & IoT**: Integration with external sensors and home automation.

---

## Instructions for AI & Codex

Codex/GPT, when reviewing, modifying, or extending this project, follow these guidelines:

* Maintain clear separation of concerns among modules.
* Preserve Java-only codebase (no Kotlin/Compose).
* Ensure any new logic adheres to the modular structure.
* Always use Dagger 2 for DI; no manual dependency creation.
* Integrate recommended libraries correctly (Glide, Timber, MPAndroidChart, RxJava).
* Expand test coverage whenever new code or features are introduced.
* Update documentation (this README) whenever major architectural or feature changes are made.

---

## Next Steps for Developers

* Validate and finalize the modular migration.
* Implement immediate feature enhancements.
* Prepare scaffolding for future expansions (AR, ML, Cloud).
* Maintain clear documentation and ensure code quality remains high.

---

**This README.md serves as the project's authoritative guide, ensuring consistency and clarity throughout the development lifecycle.**
