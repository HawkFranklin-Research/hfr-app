package com.hawkfranklin.aura.hawkfranklin.ui.project

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.hawkfranklin.aura.hawkfranklin.model.ProjectTile

@Composable
fun HawkConsentDialog(
  project: ProjectTile,
  onDismiss: () -> Unit,
  onAgree: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(project.name, fontWeight = FontWeight.Black) },
    text = {
      Text(
        "${project.longDescription}\n\nResearch consent\n${project.consentText}"
      )
    },
    confirmButton = {
      TextButton(onClick = onAgree) { Text("Agree and continue") }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Close") }
    },
  )
}
