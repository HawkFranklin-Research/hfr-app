# Google Play Publishing Notes (HawkFranklin)

This document is the HawkFranklin-specific checklist for publishing to Google Play.

## 1) Required artifact
- Google Play expects a signed release Android App Bundle (`.aab`).
- Debug APKs are for QA only.
- Release bundle output:
  - `android/app/build/outputs/bundle/release/app-release.aab`

## 2) Signing model
- Use Play App Signing.
- Keep a HawkFranklin-specific upload keystore locally.
- Sign the release bundle with the upload key.
- Google Play re-signs distribution artifacts with the app signing key it manages.

Reference:
- `https://developer.android.com/guide/publishing/app-signing`

## 3) Local signing files
- Local secret file:
  - `android/keystore.properties`
- Repo template:
  - `android/keystore.properties.example`
- Required properties:
  - `storeFile`
  - `storePassword`
  - `keyAlias`
  - `keyPassword`

## 4) First upload checklist
1. Generate a HawkFranklin upload keystore.
2. Build a signed bundle with `bundleRelease`.
3. Create the Play Console app and enroll in Play App Signing.
4. Upload the `.aab`.
5. Register the release SHA fingerprints in Firebase/Google APIs if Google Sign-In or Firebase Auth is used.

## 5) Versioning
- `versionCode` must increase on every upload.
- Update `versionCode` and `versionName` in `android/app/build.gradle.kts`.

## 6) Privacy and data safety
- HawkFranklin is a clinical research workflow app, so the Play privacy policy and Data Safety form must match the final backend and analytics setup.
- Do not reuse AURA policy text.
- Publish a dedicated HawkFranklin privacy policy URL before external testing.

## 7) Firebase / Google services
- If Firebase Analytics remains enabled, verify the Play Console disclosure requirements for analytics and advertising identifiers.
- If Google Sign-In is enabled, add the release certificate fingerprints to the Firebase project before testing a release build.

## 8) Store assets
- App icon: `512 x 512` PNG
- Feature graphic: `1024 x 500` JPG/PNG
- At least 2 phone screenshots
- Optional tablet screenshots

## 9) Build commands
From `android/`:
```bash
./gradlew clean bundleRelease
./gradlew assembleRelease
```

## 10) Troubleshooting
- "Debug signed" in Play Console means the release variant is not using the upload keystore.
- "Version code already used" means `versionCode` was not incremented.
- Google sign-in failures on release builds usually mean the release SHA fingerprint is missing in Firebase or Google Cloud.
