# HawkFranklin Firebase Setup

This guide maps the Firebase Console setup directly to `hfr-app` and to the backend pattern already used in `pelli-mobile`.

## 1. Register the Android app in Firebase

In Firebase Console, when it asks for:

- Android package name:
  `com.hawkfranklin.hawkfranklin`
- App nickname:
  `HawkFranklin Android`
- Debug signing certificate SHA-1:
  optional for the first pass, but add it if you plan to test Google Sign-In immediately

Do not use `com.company.appname`. The real package name in this repo is defined in `android/app/build.gradle.kts`.

## 2. Download and place `google-services.json`

After registration, download the Android config file and place it at:

`android/app/google-services.json`

This repo is now wired to apply the Google Services Gradle plugin only when that file exists, so placeholder builds still work before the file is added.

## 3. Firebase products to enable

Minimum recommended Firebase setup for HawkFranklin:

- Authentication
  - Enable Google as a sign-in provider
  - Add the support email
- Firestore Database
  - Start in Native mode
  - Choose a region close to your users or backend
- Analytics
  - Already present in the app as an optional integration

Optional later:

- Cloud Storage
  - Use if project workflows need image uploads instead of bundled demo assets

## 4. Values to copy into the app

Update:

`android/app/src/main/java/com/hawkfranklin/aura/hawkfranklin/HawkFranklinConfig.kt`

Replace:

- `firebaseProjectId`
- `firebaseWebClientId`
- `firestoreUsersCollection`
- `firestoreProjectsCollection`
- `firestoreResponsesCollection`
- `backendServiceAccountPath`

The `firebaseWebClientId` should come from the Web OAuth client in Firebase / Google Cloud, not the Android client ID.

## 5. SHA fingerprints you should register

For Google Sign-In and release testing, register both debug and release fingerprints in Firebase.

Current local debug SHA-1 for this machine:

`7E:10:A1:69:11:FC:AD:D9:6B:26:08:53:0C:27:2A:4A:18:BA:B8:41`

This is the fingerprint Firebase should have right now for local debug installs from this repo.

Debug keystore:

```bash
keytool -list -v \
  -alias androiddebugkey \
  -keystore ~/.android/debug.keystore \
  -storepass android \
  -keypass android
```

Release keystore:

```bash
keytool -list -v \
  -alias <your-upload-key-alias> \
  -keystore android/keystore/<your-upload-key>.jks
```

If release Google Sign-In fails but debug works, a missing release SHA fingerprint is the first thing to check.

Current release state in this repo:

- Release signing material should remain local-only and out of git
- Add your local upload keystore SHA-1 to Firebase once you create it on your machine
- After the first Play App Signing upload, also add the Play app-signing SHA-1 from Play Console

## 6. Android state in this repo

Already wired:

- Firebase app initialization in `android/app/src/hawk/java/com/hawkfranklin/aura/GalleryApplication.kt`
- Firebase Analytics fallback-safe access in `android/app/src/main/java/com/hawkfranklin/aura/Analytics.kt`
- Firebase Auth and Firestore dependencies in Gradle
- Conditional `google-services` plugin activation when `google-services.json` exists

Still placeholder:

- Real Google Sign-In UI flow
- Firebase Auth session management
- Firestore reads and writes for profile, projects, and responses

## 7. Recommended Firestore data model

For HawkFranklin, a good first-pass structure is:

- `users/{uid}`
  - name
  - email
  - role
  - institution
  - createdAt
  - lastSeenAt
- `projects/{projectId}`
  - name
  - summary
  - status
  - targetCount
  - remainingCount
  - taskType
- `responses/{responseId}`
  - projectId
  - userId
  - cardId
  - answer
  - createdAt
  - clientVersion
- `consents/{uid}`
  - acceptedAt
  - appVersion

If you expect clinician review workflows later, add role-based server checks in the backend rather than relying only on Firestore rules.

## 8. Backend recommendation

Follow the `pelli-mobile` backend pattern:

- Android app signs in with Firebase Auth
- App sends `Authorization: Bearer <firebase-id-token>` to backend
- Backend verifies the token with Firebase Admin SDK
- Backend writes canonical records to Firestore

That pattern is safer than letting the mobile app write all research records directly to Firestore, especially for:

- role checks
- audit logging
- project-level validation
- future exports and analytics pipelines

## 9. Suggested backend stack for `hfr-app`

Recommended first version:

- FastAPI on Cloud Run
- Firebase Admin SDK
- Firestore as the system of record
- Secret Manager or ADC for credentials

Core endpoints:

- `GET /healthz`
- `GET /me`
- `POST /users/onboard`
- `GET /projects`
- `POST /responses`
- `GET /responses/mine`

## 10. Play Console guidance that matters for Firebase

Before uploading a release build:

- increment `versionCode`
- build a signed `.aab`, not a debug APK
- register release SHA fingerprints in Firebase
- verify Privacy Policy and Data Safety answers against actual Firebase + backend behavior

See also:

- `PLAY_CONSOLE_GUIDE.md`
- `SETUP_PLACEHOLDERS.md`
- `android/app/google-services.json.example`
- `backend/service-account.json.example`
