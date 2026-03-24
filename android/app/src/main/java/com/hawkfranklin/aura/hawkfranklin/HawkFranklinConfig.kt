package com.hawkfranklin.aura.hawkfranklin

object HawkFranklinConfig {
  const val firebaseProjectId = "REPLACE_ME_FIREBASE_PROJECT_ID"
  const val firebaseWebClientId = "REPLACE_ME_FIREBASE_WEB_CLIENT_ID"
  const val firestoreUsersCollection = "REPLACE_ME_USERS_COLLECTION"
  const val firestoreProjectsCollection = "REPLACE_ME_PROJECTS_COLLECTION"
  const val firestoreResponsesCollection = "REPLACE_ME_RESPONSES_COLLECTION"
  const val backendServiceAccountPath = "REPLACE_WITH_BACKEND_SERVICE_ACCOUNT_JSON"

  fun isFirebaseConfigured(): Boolean {
    return !firebaseProjectId.startsWith("REPLACE_ME") &&
      !firebaseWebClientId.startsWith("REPLACE_ME")
  }
}
