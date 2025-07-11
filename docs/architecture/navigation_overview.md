# Navigation Overview

This document lists the primary fragment entry points and how the user can navigate between them.
Each feature module defines its own nav graph that is integrated into the main one in the `:app`
module.

## App Graph

- **HomeFragment** – start destination hosting the bottom navigation.
- **MeasureFragment** – accessible from Home and bottom navigation; provides live light readings.
- **PlantListFragment** – shows all saved plants. From here you can open **AddPlantFragment** or *
  *PlantDetailFragment**.
- **AddPlantFragment** – used to create a new plant entry.
- **PlantDetailFragment** – displays a single plant and links to **PlantGrowthTimelineFragment**.
- **PlantGrowthTimelineFragment** – timeline of diary events for a specific plant.
- **PlantDiaryFragment** – general diary view listing all care events.
- **ProfileFragment** – user profile and preferences.
- **SettingsFragment** – global measurement and app settings.
- **LampProfilesFragment** – manage lamp calibration profiles.
- **CalibrationFragment** – manual calibration screen.
- **CalibrationWizardFragment** – guided calibration flow.

Each feature module (`feature-measurement`, `feature-plantdb`, `feature-diary`,
`feature-growschedule`) exposes its own navigation graph which is included in the main app graph.
Actions between fragments rely on Android Navigation component IDs as defined in their respective
`res/navigation` XML files.