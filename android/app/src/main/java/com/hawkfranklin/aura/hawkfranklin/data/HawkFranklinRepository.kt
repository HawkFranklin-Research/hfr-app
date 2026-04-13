package com.hawkfranklin.aura.hawkfranklin.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hawkfranklin.aura.R
import com.hawkfranklin.aura.hawkfranklin.HawkFranklinConfig
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile
import com.hawkfranklin.aura.hawkfranklin.model.HomeMetrics
import com.hawkfranklin.aura.hawkfranklin.model.ProjectTile
import com.hawkfranklin.aura.hawkfranklin.model.QuestionType
import com.hawkfranklin.aura.hawkfranklin.model.QuestionnaireAnswer
import com.hawkfranklin.aura.hawkfranklin.model.QuestionnaireQuestion
import java.util.UUID

class HawkFranklinRepository {
  private val firestore by lazy { FirebaseFirestore.getInstance() }
  private val users by lazy { firestore.collection(HawkFranklinConfig.firestoreUsersCollection) }
  private val projects by lazy { firestore.collection(HawkFranklinConfig.firestoreProjectsCollection) }
  private val consents by lazy { firestore.collection(HawkFranklinConfig.firestoreConsentsCollection) }
  private val sessions by lazy { firestore.collection(HawkFranklinConfig.firestoreSessionsCollection) }
  private val responses by lazy { firestore.collection(HawkFranklinConfig.firestoreResponsesCollection) }

  fun ensureUserShell(
    firebaseUser: FirebaseUser,
    onResult: (ClinicianProfile) -> Unit,
  ) {
    users.document(firebaseUser.uid).get()
      .addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
          onResult(snapshot.toProfile(firebaseUser))
          return@addOnSuccessListener
        }

        val shell =
          mapOf(
            "displayName" to (firebaseUser.displayName ?: ""),
            "email" to (firebaseUser.email ?: ""),
            "institution" to "",
            "specialty" to "",
            "onboardingComplete" to false,
            "createdAt" to FieldValue.serverTimestamp(),
          )
        users.document(firebaseUser.uid).set(shell)
          .addOnSuccessListener { onResult(snapshot.toProfile(firebaseUser)) }
          .addOnFailureListener {
            onResult(
              ClinicianProfile(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                institution = "",
                specialty = "",
                onboardingComplete = false,
              )
            )
          }
      }
      .addOnFailureListener {
        onResult(
          ClinicianProfile(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            institution = "",
            specialty = "",
            onboardingComplete = false,
          )
        )
      }
  }

  fun saveProfile(
    profile: ClinicianProfile,
    onSuccess: (ClinicianProfile) -> Unit,
    onFailure: (String) -> Unit,
  ) {
    val payload =
      mapOf(
        "displayName" to profile.displayName,
        "email" to profile.email,
        "institution" to profile.institution,
        "specialty" to profile.specialty,
        "onboardingComplete" to true,
        "updatedAt" to FieldValue.serverTimestamp(),
      )
    users.document(profile.uid).set(payload)
      .addOnSuccessListener { onSuccess(profile.copy(onboardingComplete = true)) }
      .addOnFailureListener { error ->
        onFailure(error.localizedMessage ?: "Unable to save profile.")
      }
  }

  fun loadProjects(
    onSuccess: (List<ProjectTile>) -> Unit,
  ) {
    projects.get()
      .addOnSuccessListener { snapshot ->
        if (snapshot.isEmpty) {
          seedProjects(onSuccess)
        } else {
          val mapped = snapshot.documents.map { it.toProjectTile() }
          onSuccess(if (mapped.isEmpty()) seedProjectsLocal() else mapped)
        }
      }
      .addOnFailureListener {
        onSuccess(seedProjectsLocal())
      }
  }

  fun defaultProjects(): List<ProjectTile> = seedProjectsLocal()

  fun loadMetrics(uid: String, onSuccess: (HomeMetrics) -> Unit) {
    responses.whereEqualTo("uid", uid).get()
      .addOnSuccessListener { responseSnapshot ->
        consents.whereEqualTo("uid", uid).get()
          .addOnSuccessListener { consentSnapshot ->
            onSuccess(
              HomeMetrics(
                completedResponses = responseSnapshot.size(),
                activeConsents = consentSnapshot.size(),
              )
            )
          }
          .addOnFailureListener {
            onSuccess(HomeMetrics(completedResponses = responseSnapshot.size()))
          }
      }
      .addOnFailureListener {
        onSuccess(HomeMetrics())
      }
  }

  fun recordConsent(
    uid: String,
    project: ProjectTile,
    onComplete: (String?) -> Unit,
  ) {
    val consentId = "${uid}_${project.id}"
    val payload =
      mapOf(
        "uid" to uid,
        "projectId" to project.id,
        "projectName" to project.name,
        "acceptedAt" to FieldValue.serverTimestamp(),
      )
    consents.document(consentId).set(payload)
      .addOnSuccessListener { onComplete(null) }
      .addOnFailureListener {
        onComplete("Consent could not be synced yet. Continuing in app mode.")
      }
  }

  fun submitQuestionnaire(
    uid: String,
    profile: ClinicianProfile,
    project: ProjectTile,
    answers: List<QuestionnaireAnswer>,
    onComplete: (String?) -> Unit,
  ) {
    val sessionId = UUID.randomUUID().toString()
    val sessionPayload =
      mapOf(
        "uid" to uid,
        "projectId" to project.id,
        "projectName" to project.name,
        "displayName" to profile.displayName,
        "institution" to profile.institution,
        "specialty" to profile.specialty,
        "answerCount" to answers.size,
        "createdAt" to FieldValue.serverTimestamp(),
      )

    val batch = firestore.batch()
    batch.set(sessions.document(sessionId), sessionPayload)
    answers.forEach { answer ->
      val responseId = UUID.randomUUID().toString()
      batch.set(
        responses.document(responseId),
        mapOf(
          "uid" to uid,
          "sessionId" to sessionId,
          "projectId" to project.id,
          "questionId" to answer.questionId,
          "questionTitle" to answer.title,
          "value" to answer.value,
          "createdAt" to FieldValue.serverTimestamp(),
        ),
      )
    }
    batch.update(
      users.document(uid),
      mapOf(
        "lastSubmittedAt" to FieldValue.serverTimestamp(),
        "latestProjectId" to project.id,
      )
    )
    batch.commit()
      .addOnSuccessListener { onComplete(null) }
      .addOnFailureListener {
        onComplete("Answers were collected locally in this session, but cloud sync failed.")
      }
  }

  fun questionnaireFor(projectId: String): List<QuestionnaireQuestion> {
    return when (projectId) {
      "telemedicine" ->
        listOf(
          QuestionnaireQuestion(
            id = "tm-1",
            title = "Referral urgency",
            prompt = "How urgent is this telemedicine case based on the case note and symptom timing?",
            helper = "Choose the severity that best reflects the next clinical action.",
            type = QuestionType.MultipleChoice,
            options =
              listOf(
                "A. Immediate same-day clinician response",
                "B. Response within 24 hours",
                "C. Routine follow-up within 72 hours",
                "D. Educational guidance only",
              ),
          ),
          QuestionnaireQuestion(
            id = "tm-2",
            title = "Expected callback window",
            prompt = "Enter the maximum callback window you would allow before this case becomes overdue.",
            helper = "Use a number of hours.",
            type = QuestionType.Numeric,
            placeholder = "e.g. 24",
          ),
          QuestionnaireQuestion(
            id = "tm-3",
            title = "Consult note",
            prompt = "Write the brief handoff note you would want another clinician to read before opening the chart.",
            helper = "Focus on triage logic, safety flags, and what should happen next.",
            type = QuestionType.Text,
            placeholder = "Short triage or escalation note",
          ),
        )

      else ->
        listOf(
          QuestionnaireQuestion(
            id = "derma-1",
            title = "Primary pattern match",
            prompt = "Which option best matches the visible lesion pattern shown in these sample images?",
            helper = "This mirrors the kind of label collection Derma AI will use for structured review.",
            type = QuestionType.MultipleChoice,
            options =
              listOf(
                "A. Atopic dermatitis / eczema flare",
                "B. Psoriatic plaque morphology",
                "C. Tinea corporis ring pattern",
                "D. Herpes zoster distribution",
              ),
            images = listOf(R.drawable.eczema_1, R.drawable.eczema_2),
          ),
          QuestionnaireQuestion(
            id = "derma-2",
            title = "Estimated duration",
            prompt = "How many days has this lesion pattern likely been active, based on the written history?",
            helper = "Use an integer estimate.",
            type = QuestionType.Numeric,
            placeholder = "e.g. 14",
          ),
          QuestionnaireQuestion(
            id = "derma-3",
            title = "Differential note",
            prompt = "Write the concise differential or caution note you would attach for downstream review.",
            helper = "Use one or two sentences.",
            type = QuestionType.Text,
            placeholder = "Differential or caution note",
          ),
        )
    }
  }

  private fun seedProjects(onSuccess: (List<ProjectTile>) -> Unit) {
    val seeds = seedProjectsLocal()
    val batch = firestore.batch()
    seeds.forEach { project ->
      batch.set(
        projects.document(project.id),
        mapOf(
          "name" to project.name,
          "shortDescription" to project.shortDescription,
          "longDescription" to project.longDescription,
          "consentText" to project.consentText,
          "accentHex" to project.accentHex,
          "logoKey" to project.logoKey,
          "questionCount" to project.questionCount,
          "liveStatus" to project.liveStatus,
          "updatedAt" to FieldValue.serverTimestamp(),
        ),
      )
    }
    batch.commit()
      .addOnSuccessListener { onSuccess(seeds) }
      .addOnFailureListener { onSuccess(seeds) }
  }

  private fun seedProjectsLocal(): List<ProjectTile> {
    return listOf(
      ProjectTile(
        id = "derma_ai",
        name = "Derma AI",
        shortDescription = "Case-based dermatology labeling and review feed.",
        longDescription =
          "Structured dermoscopy and lesion review cases for clinicians. This stream is intended for annotation quality checks, differential capture, and model evaluation against real-world review behavior.",
        consentText =
          "By continuing, you agree that your answers and any case-level interpretation you submit may be used for internal research, model evaluation, and dataset quality improvement. No patient identifiers should be entered into the response fields.",
        accentHex = "#C9722B",
        logoKey = "derma",
        questionCount = 3,
        liveStatus = "Live dataset collection",
      ),
      ProjectTile(
        id = "telemedicine",
        name = "Telemedicine",
        shortDescription = "Remote triage reasoning and clinician handoff practice.",
        longDescription =
          "Telemedicine scenarios focused on response urgency, remote escalation, and concise clinician-to-clinician summaries. This stream is designed for quality review on digital-first workflows.",
        consentText =
          "By continuing, you agree that your triage choices, response timing estimates, and handoff notes may be collected for internal workflow research and operations analysis. Do not include direct patient identifiers in your answers.",
        accentHex = "#2F6CE5",
        logoKey = "telemedicine",
        questionCount = 3,
        liveStatus = "Pilot workflow live",
      ),
    )
  }

  private fun com.google.firebase.firestore.DocumentSnapshot.toProfile(
    firebaseUser: FirebaseUser,
  ): ClinicianProfile {
    return ClinicianProfile(
      uid = firebaseUser.uid,
      displayName = getString("displayName") ?: firebaseUser.displayName.orEmpty(),
      email = getString("email") ?: firebaseUser.email.orEmpty(),
      institution = getString("institution").orEmpty(),
      specialty = getString("specialty").orEmpty(),
      onboardingComplete = getBoolean("onboardingComplete") ?: false,
    )
  }

  private fun com.google.firebase.firestore.DocumentSnapshot.toProjectTile(): ProjectTile {
    return ProjectTile(
      id = id,
      name = getString("name") ?: "Untitled project",
      shortDescription = getString("shortDescription") ?: "",
      longDescription = getString("longDescription") ?: "",
      consentText = getString("consentText") ?: "",
      accentHex = getString("accentHex") ?: "#44556B",
      logoKey = getString("logoKey") ?: "derma",
      questionCount = (getLong("questionCount") ?: 0L).toInt(),
      liveStatus = getString("liveStatus") ?: "Draft",
    )
  }
}
