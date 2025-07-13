# Firebase Setup

To enable Firestore sync, create a Firebase project and register an Android app with the package
name `de.omagh.lumibuddy`.

1. Download the generated `google-services.json` from the Firebase console.
2. Place it in the `app/` directory of this repository so the path is `app/google-services.json`.
3. In the Firebase console enable an authentication method (Email/Password or Anonymous) under
   *Authentication → Sign‑in method*.
4. Enable **Firestore Database** and start it in *test mode* for local development.
5. Rebuild the project. Gradle will automatically configure Firebase using this file.

If the file is absent, Firebase components will initialise but network requests will fail. For CI
builds you can provide an empty stub file to satisfy Gradle.

## Enable Authentication

1. In the Firebase console open **Authentication → Sign-in method**.
2. Enable the **Anonymous** provider so the app can silently sign in.
3. Optionally enable **Email/Password** accounts for manual login.

## Enable Firestore

1. Navigate to **Firestore Database** and click **Create database**.
2. Choose **Start in test mode** for initial development and select your region.
3. Firestore will create default security rules and an empty database.
4. Open the **Rules** tab and replace the default rules with:

```rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

These rules require the user to be authenticated before any read or write occurs. Review them before
going to production.

5. After saving the rules, verify the **Indexes** tab shows no errors and add composite indexes as
   needed by your queries.

Once authentication is enabled and the `google-services.json` file is placed correctly, the app can
synchronise data with Firestore securely.