# LumiBud 🌱 – Smart Plant Light Measurement & Care App

## Overview

**LumiBud** is a modular, future-proof Android app for plant lovers, hobby growers, and professionals.  
It combines professional-grade light/environment measurement, plant identification, personalized care tracking, and actionable recommendations – all in a modern, beautiful UI.

## Key Features

- **Accurate plant-centric light measurement:** Lux, PPFD, DLI, spectrum, and CCT, using phone sensors/camera with calibration.
- **Plant identification:** AI-powered auto-ID and manual search.
- **Personalized diary:** Track growth, care events, watering, environment, and compare progress with calendar/agenda views.
- **Smart recommendations:** Get actionable, plant-specific care advice (lighting, watering, humidity, temperature, interventions).
- **Grow light auto-ID & calibration:** Scan/search for lamp type and specs or enter manually, supporting best measurement accuracy.
- **Periodic scans:** Reminders and visual growth tracking with photos and metrics.
- **Comprehensive settings:** All app, measurement, calibration, and profile preferences in one place.
- **Planned:** AR overlays, ML-based plant/lamp recognition, and external hardware sensor support (Bluetooth/USB/IoT).

---

## Table of Contents

- [Project Vision](#project-vision)
- [App Architecture](#app-architecture)
- [Core Modules and Responsibilities](#core-modules-and-responsibilities)
- [Major Features (Current & Planned)](#major-features-current--planned)
- [Measurement Science & Algorithms](#measurement-science--algorithms)
- [Plant Database & Care Recommendation System](#plant-database--care-recommendation-system)
- [Grow Light Product Integration](#grow-light-product-integration)
- [Modular Design for AR, ML, and Hardware](#modular-design-for-ar-ml-and-hardware)
- [UI/UX Principles](#uiux-principles)
- [Data Models](#data-models)
- [Development Plan / Roadmap](#development-plan--roadmap)
- [Contribution Guidelines](#contribution-guidelines)
- [References](#references)

---

## Project Vision

LumiBud aims to **demystify indoor plant care**, putting expert-level light/environment analysis and plant-specific recommendations in every grower’s pocket – whether they use only their phone or add advanced sensors and AI.

---

## App Architecture

### Key Principles

- **Feature modularization** for clean separation, testability, and scalability.
- **MVVM/Clean Architecture**: ViewModels, repositories, data abstraction.
- **Dependency injection** for swappable components.
- **Pluggable sensor sources** (phone, Bluetooth, USB, cloud, etc).
- **Interfaces for AR and ML modules** (can be stubbed at first, plugged in later).
- **Room database** for user/plant/care/event storage.
- **Abstracted network/API layer** for plant/lamp DBs, cloud sync, etc.
- **All logic unit-testable and UI-independent.**

### Directory Structure (Example)
de.omagh.lumibud/
├── core/ # Common utilities/constants
├── feature_measurement/ # Light/Env measurement logic
├── feature_plantdb/ # Plant database, ID, care
├── feature_diary/ # Plant diary/growth tracking
├── feature_recommendation/# Smart advice/scheduling
├── feature_growlight/ # Grow light product/ID/calibration
├── feature_user/ # User/profile/settings/calibration
├── feature_ar/ # (Future) AR overlays & guides
├── feature_ml/ # (Future) ML-based plant/light/health recognition
├── data/ # Room DB, DAOs, entities
├── network/ # Retrofit/API clients
├── ui/ # Activities/Fragments/ViewModels
├── util/ # Utility classes
├── resources/ # Layouts, drawables, values

---

## Core Modules and Responsibilities

| Module                  | Responsibility                                             | Example Classes                   |
|-------------------------|-----------------------------------------------------------|-----------------------------------|
| Measurement             | All light/temp/humidity measurements, calibration         | `MeasurementEngine`, `CameraLightMeter`, `ALSManager`, `CalibrationManager` |
| Plant DB                | Plant info, identification, care profiles                 | `PlantDatabaseManager`, `PlantIdentifier`, `PlantCareProfile`               |
| Diary                   | Plant logs, growth photos, calendar/agenda                | `PlantLogManager`, `DiaryEntry`, `AgendaManager`                            |
| Recommendation          | Care recommendations, scheduling, notifications           | `RecommendationEngine`, `WateringScheduler`                                 |
| Grow Light              | Lamp profiles, auto-ID, calibration, manual entry         | `GrowLightProfileManager`, `LampAutoIdentifier`                             |
| User/Settings           | User profile, preferences, calibration profiles           | `UserProfileManager`, `SettingsManager`                                     |
| AR                      | (Future) AR overlays/visuals for measurement/growth scan  | `ARMeasureOverlay`, `ARGrowthTracker`                                       |
| ML                      | (Future) Plant/lamp/health recognition via ML             | `PlantClassifier`, `LampTypeClassifier`                                     |
| External Hardware       | (Future) BLE/USB/IoT sensors, smart pots, etc             | `BluetoothPARMeterSource`, `USBQuantumSensorSource`, `HardwareManager`      |

---

## Major Features (Current & Planned)

### 🌟 **Current**
- Modern home/dashboard, measure tab, plant diary, growth calendar/agenda
- Accurate Lux, PPFD, DLI, CCT, spectrum measurement (camera/diffuser & ALS)
- Auto/manual plant identification, database of care needs by species/stage
- Watering schedule, care recommendations, notifications/reminders
- Grow light product integration (search, scan, manual entry)
- Periodic plant scan & visual growth tracking

### 🛠️ **Planned / Modular**
- AR overlays (real-time optical indicators for measurement/growth)
- ML-based plant and grow light auto-ID
- External hardware support (Bluetooth/USB/IoT sensors, cloud)
- Cloud backup/sync, multi-device profiles
- Advanced diagnostics: ML for plant health/disease, leaf area, etc.

---

## Measurement Science & Algorithms

### Key Metrics
- **Lux (Illuminance)**: Measured via ALS or camera.
- **Foot-candles**: Optional imperial unit (1 fc = 10.764 lux).
- **PPFD (μmol/m²/s)**: Core metric for plant photosynthesis.
- **DLI (mol/m²/day)**: Total plant-usable light per day.
- **CCT (Kelvin)**: Color temperature.
- **Spectrum**: R/G/B proportion; spectrum type auto-identified or manually selected.

### Algorithms/Approach

- **ALS (Ambient Light Sensor):** Direct lux reading; spectrum-dependent conversion to PPFD.
- **Camera + Diffuser:** Average RGB, spectrum heuristics, per-lamp calibration factor or user-supplied calibration.
- **Calibration:** Per-device, per-lamp, user-adjustable profiles; supports custom reference (sun, meter, manufacturer).
- **Periodic logging:** For DLI and environment tracking.
- **Spectral heuristics:** Rule-based RGB analysis for light type (Bluer, Redder, Full-spectrum, etc).

### Modular Input Source Design

- All measurements via `LightMeasurementSource` interface (phone, BLE, USB, IoT, etc).
- Easy to extend for hardware with minimal core code changes.

---

## Plant Database & Care Recommendation System

- **Plant ID:** AI photo recognition (ML Kit, PlantID API), manual search/list as fallback.
- **Plant DB:** Optimal DLI, PPFD, temp, humidity, watering by species/stage.
- **Diary:** Growth scans, photos, measurements, notes, interventions.
- **Care Recommendation Engine:** Matches logs/measurements to plant needs; gives user actionable, stage-specific advice.
- **Watering/Fertilizer Scheduler:** Dynamic reminders, rescheduling based on log entries.

---

## Grow Light Product Integration

- **Auto-ID:** Scan/search lamp brand/model/barcode or take photo; fetch lamp specs from DB/online.
- **Manual Entry:** User can specify lamp type, spectrum, wattage, PPFD at distance.
- **Calibration:** Each lamp/profile can have unique calibration factor.

---

## Modular Design for AR, ML, and Hardware

- **AR Module:** Overlays for live measurement, growth alignment, plant care suggestions (via ARCore or compatible tech).
- **ML Module:** Plant/lamp recognition, health status detection (modular, replaceable, updatable as tech advances).
- **External Hardware Module:** Bluetooth/USB/IoT sensors; plug-and-play via interface and HardwareManager.

---

## UI/UX Principles

- Modern, clean, minimal UI (Material 3 style)
- Dashboard with today’s status, reminders, quick actions
- Diary/calendar/agenda for all logs and tasks
- Simple and attractive “Add Plant”/“Scan Now” flows
- Visual spectrum, DLI, and growth charts (with photo before/after)
- All preferences/calibration/settings easily discoverable
- All features accessible without AR/ML/hardware, but ready for them

---

## Data Models (Example)

```java
class Plant {
    String id, nickname, scientificName, commonName, photoUri;
    PlantStage currentStage;
    PlantCareProfile careProfile;
    List<PlantLogEntry> diary;
}

class PlantLogEntry {
    Date timestamp;
    Measurement measurement;
    String note;
    String photoUri;
    boolean watered, interventionRequired;
}

class Measurement {
    float lux, footcandles, ppfd, dli, cct, temperature, humidity;
    SpectrumEstimate spectrum;
    String source; // "Phone Camera", "BLE PAR Meter", etc
    String calibrationProfileId;
}
Development Plan / Roadmap
Project foundation: App skeleton, navigation, MVVM, Room DB

Measurement MVP: ALS/camera/diffuser, core metric display

Plant DB/Diary: Plant add/ID/search, care DB, logs, timeline/calendar

Grow light/lamp integration: Search/scan/manual entry, calibration manager

Care recommendations & reminders: Scheduler, notifications

UI/UX polish: Modern dashboard, diary, photo comparison

AR/ML stubs: Placeholders for future overlays/recognition

Hardware modularization: Interface and code for external sensor expansion

Advanced features: AR overlays, ML health/plant/lamp recognition, cloud sync, export/import

Contribution Guidelines
Keep all new features modular (new package/module per major feature)

Use interfaces for all external services (easy to mock/replace)

Document all new public methods and data flows

Write and run unit tests for each feature

Update README with all major changes or planned extensions

References
Photone Whitepaper (iOS/Android Light Meter Science)

[Smartphone-Based Light Measurement for Indoor Plant Care (Research PDF)]

growlightmeter.com

PlantID API

PlantNet

ARCore for Android

Android ML Kit

Notes
This README is a living document.
As LumiBud evolves, update this file to reflect architecture, data model, and major feature additions or changes.

For questions, ideas, or collaboration, [contact project owner] or open an issue.
