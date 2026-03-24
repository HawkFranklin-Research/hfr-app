# HawkFranklin

Internal research data-collection starter app built by repurposing the AURA Android shell.

## Current Starter Scope
- Light-themed Android app shell with HawkFranklin branding
- Placeholder Google/Firebase auth gate
- One-time local onboarding flow for clinician profile capture
- Dashboard with ongoing research project panels
- Demo dermatology flashcard project with bundled local sample images
- Local counters for:
  - cases viewed
  - labels submitted

## What is Placeholder Today
- Real Google Sign-In
- Firebase Auth session handling
- Firestore user/project/response persistence
- Backend service account integration

## Important Placeholder Files
- `android/app/google-services.json.example`
- `android/app/src/main/java/com/hawkfranklin/aura/hawkfranklin/HawkFranklinConfig.kt`
- `backend/service-account.json.example`
- `SETUP_PLACEHOLDERS.md`

## Build
```bash
cd android
./gradlew assembleDebug
```

If Firebase is not configured yet, keep the app in placeholder mode and do not add the real `google-services.json` until those values are available.
