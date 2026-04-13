package com.hawkfranklin.aura.hawkfranklin.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hawkfranklin.aura.R
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile
import com.hawkfranklin.aura.hawkfranklin.model.HomeMetrics
import com.hawkfranklin.aura.hawkfranklin.model.ProjectTile

@Composable
fun HawkHomeScreen(
  authenticated: Boolean,
  profile: ClinicianProfile?,
  metrics: HomeMetrics,
  projects: List<ProjectTile>,
  projectsLoading: Boolean,
  syncMessage: String?,
  onProfileClick: () -> Unit,
  onProjectClick: (ProjectTile) -> Unit,
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier =
      Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFCF6), Color(0xFFF6F0E7), Color(0xFFFFFFFF))
          )
        )
        .statusBarsPadding()
        .navigationBarsPadding(),
    contentPadding = PaddingValues(20.dp),
    horizontalArrangement = Arrangement.spacedBy(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Image(
            painter = painterResource(R.drawable.hawkfranklin_logo),
            contentDescription = "HawkFranklin logo",
            modifier = Modifier.size(52.dp),
          )
          Column {
            Text(
              "HawkFranklin",
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            )
            Text(
              if (authenticated) {
                profile?.let { "${it.displayName} • ${it.specialty.ifBlank { "Clinician" }}" }
                  ?: "Signed in clinician workspace"
              } else {
                "Internal clinical data collection workspace"
              },
              style =
                MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
          }
        }
        IconButton(
          onClick = onProfileClick,
          modifier =
            Modifier
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.92f)),
        ) {
          Icon(Icons.Outlined.Person, contentDescription = "Open profile drawer")
        }
      }
    }

    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            if (authenticated) "Ongoing projects" else "Clinical research feed",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
          )
          Text(
            "This internal app is built for structured case review, telemedicine workflow practice, and dataset-quality collection across changing research projects.",
            style =
              MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )
          if (!authenticated) {
            Text(
              "Open a project tile to review the consent summary. If you are not signed in, the app will route you into authentication before the questionnaire starts.",
              style =
                MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
          }
        }
      }
    }

    item {
      MetricCard(title = "Responses logged", value = metrics.completedResponses.toString())
    }
    item {
      MetricCard(title = "Consents recorded", value = metrics.activeConsents.toString())
    }

    if (syncMessage != null) {
      item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
        Card(
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, Color(0xFFFFD5C2)),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F3)),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Outlined.SyncProblem, contentDescription = null, tint = Color(0xFFC2410C))
            Text(
              syncMessage,
              style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF9A3412)),
            )
          }
        }
      }
    }

    if (projectsLoading) {
      item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
        Box(
          modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
    } else {
      items(projects) { project ->
        ProjectTileCard(project = project, onClick = { onProjectClick(project) })
      }
    }
  }
}

@Composable
private fun MetricCard(title: String, value: String) {
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
      )
      Text(
        title,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
      )
    }
  }
}

@Composable
private fun ProjectTileCard(project: ProjectTile, onClick: () -> Unit) {
  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = project.accentColor().copy(alpha = 0.14f)),
    border = BorderStroke(1.dp, project.accentColor().copy(alpha = 0.28f)),
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(18.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
        modifier =
          Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.82f)),
            contentAlignment = Alignment.Center,
      ) {
        Icon(
          painter = painterResource(project.logoRes()),
          contentDescription = null,
          tint = project.accentColor(),
        )
      }
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          project.name,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
        )
        Text(
          project.shortDescription,
          style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF344054)),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          "${project.questionCount} questions",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
          project.liveStatus,
          style =
            MaterialTheme.typography.labelMedium.copy(
              color = project.accentColor(),
              fontWeight = FontWeight.Bold,
            ),
        )
      }
    }
  }
}

private fun ProjectTile.logoRes(): Int {
  return when (logoKey) {
    "telemedicine" -> R.drawable.ic_project_telemedicine
    else -> R.drawable.ic_project_derma
  }
}

private fun ProjectTile.accentColor(): Color {
  return runCatching {
      Color(android.graphics.Color.parseColor(accentHex))
    }
    .getOrElse { Color(0xFF2F6CE5) }
}
