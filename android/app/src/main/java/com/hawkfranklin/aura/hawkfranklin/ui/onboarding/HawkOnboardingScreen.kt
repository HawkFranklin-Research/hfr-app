package com.hawkfranklin.aura.hawkfranklin.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile

@Composable
fun HawkOnboardingScreen(
  firebaseUser: FirebaseUser,
  existingProfile: ClinicianProfile?,
  onBack: () -> Unit,
  onSave: (displayName: String, institution: String, specialty: String) -> Unit,
) {
  var displayName by remember {
    mutableStateOf(existingProfile?.displayName?.ifBlank { firebaseUser.displayName.orEmpty() } ?: firebaseUser.displayName.orEmpty())
  }
  var institution by remember { mutableStateOf(existingProfile?.institution.orEmpty()) }
  var specialty by remember { mutableStateOf(existingProfile?.specialty.orEmpty()) }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    IconButton(onClick = onBack) {
      Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }

    Text(
      "Clinician onboarding",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
    )
    Text(
      "Complete the single user profile used across Derma AI, Telemedicine, and future internal project streams.",
      style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
    )

    OutlinedTextField(
      value = displayName,
      onValueChange = { displayName = it },
      label = { Text("Full name") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
    )
    OutlinedTextField(
      value = firebaseUser.email.orEmpty(),
      onValueChange = {},
      label = { Text("Signed-in email") },
      modifier = Modifier.fillMaxWidth(),
      enabled = false,
      singleLine = true,
    )
    OutlinedTextField(
      value = institution,
      onValueChange = { institution = it },
      label = { Text("Institution or department") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
    )
    OutlinedTextField(
      value = specialty,
      onValueChange = { specialty = it },
      label = { Text("Specialty or role") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
    )

    Button(
      onClick = { onSave(displayName.trim(), institution.trim(), specialty.trim()) },
      modifier = Modifier.fillMaxWidth().height(56.dp),
      enabled = displayName.isNotBlank() && institution.isNotBlank() && specialty.isNotBlank(),
    ) {
      Text("Save profile and continue")
    }
  }
}
