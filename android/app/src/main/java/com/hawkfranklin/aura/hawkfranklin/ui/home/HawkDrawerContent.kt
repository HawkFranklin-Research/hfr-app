package com.hawkfranklin.aura.hawkfranklin.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.hawkfranklin.aura.R
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile

@Composable
fun HawkDrawerContent(
  firebaseUser: FirebaseUser?,
  profile: ClinicianProfile?,
  onLogin: () -> Unit,
  onSettings: () -> Unit,
  onCompleteProfile: () -> Unit,
  onLogout: () -> Unit,
) {
  ModalDrawerSheet {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
          painter = painterResource(R.drawable.hawkfranklin_logo),
          contentDescription = null,
          modifier = Modifier.size(42.dp),
        )
        Column {
          Text(
            "HawkFranklin",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
          )
          Text(
            "Clinician workspace drawer",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
          )
        }
      }

      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6F1)),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Outlined.Person,
              contentDescription = null,
              modifier = Modifier.background(Color.White, CircleShape).padding(10.dp),
            )
            Column {
              Text(
                profile?.displayName?.ifBlank { firebaseUser?.displayName ?: "Research user" }
                  ?: "Guest user",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              )
              Text(
                profile?.email ?: "Sign in to sync profile, consent, and responses",
                style =
                  MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
              )
            }
          }
          if (firebaseUser != null && profile?.onboardingComplete != true) {
            Button(onClick = onCompleteProfile, modifier = Modifier.fillMaxWidth()) {
              Text("Complete clinician profile")
            }
          }
        }
      }

      if (firebaseUser == null) {
        DrawerActionRow(
          icon = Icons.Outlined.Login,
          label = "Login",
          description = "Use Google or email/password to join the app.",
          onClick = onLogin,
        )
      }

      DrawerActionRow(
        icon = Icons.Outlined.Settings,
        label = "Settings",
        description = "Open app status, sync details, and account information.",
        onClick = onSettings,
      )

      if (firebaseUser != null) {
        DrawerActionRow(
          icon = Icons.Outlined.Logout,
          label = "Sign out",
          description = "Return to guest mode and clear the live session.",
          onClick = onLogout,
        )
      }
    }
  }
}

@Composable
private fun DrawerActionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  description: String,
  onClick: () -> Unit,
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(icon, contentDescription = null)
      Column {
        Text(label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(
          description,
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        )
      }
    }
  }
}
