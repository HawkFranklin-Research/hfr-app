# HawkFranklin Placeholder Setup

This starter app is intentionally wired in placeholder mode so the UI can be repurposed before backend decisions are finalized.

Replace these exact locations when Firebase and cloud infrastructure are ready:

1. `android/app/google-services.json`
   Drop the Android Firebase config here.

2. `android/app/src/main/java/com/hawkfranklin/aura/hawkfranklin/HawkFranklinConfig.kt`
   Replace all `REPLACE_ME_*` values for:
   - Firebase project ID
   - Google web client ID
   - Firestore collection names
   - backend service account path reference

3. `backend/service-account.json`
   Reserved placeholder path for a future backend or Cloud Run service account JSON.

Current starter behavior:
- Google sign-in is represented by a placeholder gate.
- onboarding is stored locally on-device.
- project responses and user counters are stored locally on-device.
- the dermatology flashcard deck is bundled from local sample images.
