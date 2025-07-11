# Firebase Setup

To enable Firestore sync, create a Firebase project and register an Android app with the package
name `de.omagh.lumibuddy`.

1. Download the generated `google-services.json` from the Firebase console.
2. Place it in the `app/` directory of this repository so the path is `app/google-services.json`.
3. Rebuild the project. Gradle will automatically configure Firebase using this file.

If the file is absent, Firebase components will initialise but network requests will fail. For CI
builds you can provide an empty stub file to satisfy Gradle.