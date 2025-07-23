# Release Build Guide

This page describes how to create a signed APK or App Bundle for distribution.

## 1. Generate a keystore

Use the `keytool` command from the JDK to create a keystore file. Example:

```bash
keytool -genkeypair -v -keystore lumibuddy.jks -alias lumibuddy \
    -keyalg RSA -keysize 2048 -validity 10000
```

Store the resulting `lumibuddy.jks` somewhere safe and keep a backup.

## 2. Configure `gradle.properties`

Add your signing credentials to `gradle.properties` (either in the project root or in `~/.gradle/gradle.properties`).
Do **not** commit passwords or keystore files to version control.

```properties
RELEASE_STORE_FILE=/absolute/path/to/lumibuddy.jks
RELEASE_STORE_PASSWORD=myStorePassword
RELEASE_KEY_ALIAS=lumibuddy
RELEASE_KEY_PASSWORD=myKeyPassword
```

## 3. Build the release

To generate a signed APK run:

```bash
./gradlew assembleRelease
```

For the Google Play App Bundle use:

```bash
./gradlew bundleRelease
```

## 4. Output location

The signed artifacts are written to:

- `app/build/outputs/apk/release/` – signed APK
- `app/build/outputs/bundle/release/` – signed AAB

## 5. Beta testing & distribution

After generating a signed build you can upload it to Google Play or share it via
another service for testers.

### Google Play internal testing

1. Open the **Google Play Console** for your app.
2. Select **Testing > Internal testing** and choose *Create new release*.
3. Upload the signed AAB or APK and add release notes.
4. Add tester Gmail addresses or upload a CSV list.
5. Roll out the release to send invitation emails.

### Other distribution options

You may distribute the APK through services such as **Firebase App
Distribution** or by providing a direct download link.

### Gathering feedback

Encourage testers to report issues via the GitHub Issues page or a shared
feedback form linked in the invitation email.

Following these instructions will ensure testers can install and review your release properly.
