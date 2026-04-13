package com.hawkfranklin.aura.hawkfranklin

object HawkFranklinConfig {
  const val firebaseProjectId = "hfr-app-6c756"
  const val firestoreUsersCollection = "users"
  const val firestoreProjectsCollection = "projects"
  const val firestoreResponsesCollection = "responses"
  const val firestoreConsentsCollection = "consents"
  const val firestoreSessionsCollection = "questionnaire_sessions"
  const val backendBaseUrl = "http://10.0.2.2:8000/"
  const val backendServiceAccountPath = "backend/service-account.json"

  fun isFirebaseConfigured(): Boolean {
    return !firebaseProjectId.startsWith("REPLACE_ME")
  }

  fun isBackendConfigured(): Boolean {
    return backendBaseUrl.startsWith("http")
  }
}
