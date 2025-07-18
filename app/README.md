# App Module

## Overview
The `app` module is the entry point for the Android application. It configures application level components, sets up navigation and provides activities that host the different feature screens. Dagger is bootstrapped here by creating the `CoreComponent` and the app specific `AppComponent`.

## Key Components
- **`LumiBuddyApplication`** – Initializes `CoreComponent` and `AppComponent` and schedules background sync.
- **`AppComponent`** – Dagger component that depends on `CoreComponent` and injects the application and `MainActivity`.
- **`MainActivity`** – Hosts the navigation graph and bottom navigation bar.
- **`OnboardingActivity`** – Handles first run permissions and anonymous sign in.
- **`PrivacyPolicyActivity`** – Displays the privacy policy content.

## Dependencies
The module depends on `core-domain`, `core-data`, `core-infra` and the various feature modules such as `feature-measurement`, `feature-plantdb`, `feature-diary` and `feature-growschedule`.

For an overview of the dependency injection setup see [docs/architecture/dagger_graph.md](../docs/architecture/dagger_graph.md).
