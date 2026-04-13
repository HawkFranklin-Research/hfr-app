package com.hawkfranklin.aura.hawkfranklin.model

import androidx.annotation.DrawableRes

enum class QuestionType {
  MultipleChoice,
  Numeric,
  Text,
}

data class ClinicianProfile(
  val uid: String,
  val displayName: String,
  val email: String,
  val institution: String,
  val specialty: String,
  val onboardingComplete: Boolean,
)

data class HomeMetrics(
  val completedResponses: Int = 0,
  val activeConsents: Int = 0,
)

data class ProjectTile(
  val id: String,
  val name: String,
  val shortDescription: String,
  val longDescription: String,
  val consentText: String,
  val accentHex: String,
  val logoKey: String,
  val questionCount: Int,
  val liveStatus: String,
)

data class QuestionnaireQuestion(
  val id: String,
  val title: String,
  val prompt: String,
  val helper: String,
  val type: QuestionType,
  val options: List<String> = emptyList(),
  @DrawableRes val images: List<Int> = emptyList(),
  val placeholder: String = "",
)

data class QuestionnaireAnswer(
  val questionId: String,
  val title: String,
  val value: String,
)
