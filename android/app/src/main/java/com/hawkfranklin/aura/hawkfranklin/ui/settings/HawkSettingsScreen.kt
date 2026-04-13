package com.hawkfranklin.aura.hawkfranklin.ui.settings

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.hawkfranklin.aura.hawkfranklin.HawkFranklinConfig
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile

@Composable
fun HawkSettingsScreen(
  firebaseUser: FirebaseUser?,
  profile: ClinicianProfile?,
  syncMessage: String?,
  onBack: () -> Unit,
  onLogout: () -> Unit,
) {
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

    Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))

    SettingsCard(
      title = "Account",
      body =
        listOf(
          "Email: ${firebaseUser?.email ?: "Guest"}",
          "Name: ${profile?.displayName ?: "Not completed"}",
          "Institution: ${profile?.institution?.ifBlank { "Not set" } ?: "Not set"}",
          "Specialty: ${profile?.specialty?.ifBlank { "Not set" } ?: "Not set"}",
        ),
    )

    SettingsCard(
      title = "Sync targets",
      body =
        listOf(
          "Firebase project: ${HawkFranklinConfig.firebaseProjectId}",
          "Projects collection: ${HawkFranklinConfig.firestoreProjectsCollection}",
          "Responses collection: ${HawkFranklinConfig.firestoreResponsesCollection}",
          "Backend base URL: ${HawkFranklinConfig.backendBaseUrl}",
        ),
    )

    if (syncMessage != null) {
      SettingsCard(title = "Latest sync status", body = listOf(syncMessage), accent = Color(0xFFB45309))
    }

    if (firebaseUser != null) {
      Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth().height(56.dp),
      ) {
        Text("Sign out")
      }
    }
  }
}

@Composable
private fun SettingsCard(title: String, body: List<String>, accent: Color = Color.Unspecified) {
  Card(
    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        title,
        style =
          MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = if (accent == Color.Unspecified) MaterialTheme.colorScheme.onSurface else accent,
          ),
      )
      body.forEach { line ->
        Text(
          line,
          style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        )
      }
    }
  }
}
