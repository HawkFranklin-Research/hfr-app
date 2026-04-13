package com.hawkfranklin.aura.hawkfranklin

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hawkfranklin.aura.R
import kotlin.random.Random

private const val PREFS_NAME = "hawkfranklin_research"
private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
private const val KEY_NAME = "name"
private const val KEY_EMAIL = "email"
private const val KEY_ROLE = "role"
private const val KEY_INSTITUTION = "institution"
private const val KEY_VIEWED = "viewed_count"
private const val KEY_SUBMITTED = "submitted_count"

private enum class HawkFranklinScreen {
  AUTH,
  ONBOARDING,
  DASHBOARD,
  PROJECT,
}

private data class ClinicianProfile(
  val name: String,
  val email: String,
  val role: String,
  val institution: String,
)

private data class ResearchStats(
  val viewedCount: Int,
  val submittedCount: Int,
)

private data class DemoProject(
  val id: String,
  val name: String,
  val summary: String,
  val remainingForPaper: Int,
  val totalForPaper: Int,
  val demoReady: Boolean,
  val accent: Color,
)

private data class FlashcardCase(
  val id: String,
  val title: String,
  val prompt: String,
  @DrawableRes val images: List<Int>,
  val options: List<String>,
)

@Composable
fun HawkFranklinApp() {
  val context = LocalContext.current
  val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
  var screen by remember { mutableStateOf(HawkFranklinScreen.AUTH) }
  var profile by remember { mutableStateOf(loadProfile(prefs)) }
  var stats by remember { mutableStateOf(loadStats(prefs)) }
  var selectedProjectId by remember { mutableStateOf<String?>(null) }
  val projects = remember(stats.submittedCount) { buildProjects(stats.submittedCount) }
  val demoCards = remember {
    buildDemoCards().shuffled(Random(System.currentTimeMillis()))
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background,
  ) {
    when (screen) {
      HawkFranklinScreen.AUTH ->
        AuthGateScreen(
          onContinue = {
            screen =
              if (profile == null || !prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)) {
                HawkFranklinScreen.ONBOARDING
              } else {
                HawkFranklinScreen.DASHBOARD
              }
          },
        )

      HawkFranklinScreen.ONBOARDING ->
        OnboardingScreen(
          initialProfile = profile,
          onBack = { screen = HawkFranklinScreen.AUTH },
          onComplete = { updated ->
            profile = updated
            saveProfile(prefs, updated)
            screen = HawkFranklinScreen.DASHBOARD
          },
        )

      HawkFranklinScreen.DASHBOARD ->
        DashboardScreen(
          profile = profile,
          stats = stats,
          projects = projects,
          onOpenProject = { project ->
            if (project.demoReady) {
              selectedProjectId = project.id
              screen = HawkFranklinScreen.PROJECT
            }
          },
          onSwitchUser = { screen = HawkFranklinScreen.AUTH },
        )

      HawkFranklinScreen.PROJECT -> {
        val project = projects.firstOrNull { it.id == selectedProjectId } ?: projects.first()
        ProjectFlashcardScreen(
          project = project,
          cards = demoCards,
          stats = stats,
          onBack = { screen = HawkFranklinScreen.DASHBOARD },
          onStatsChanged = { viewedDelta, submittedDelta ->
            stats =
              stats.copy(
                viewedCount = stats.viewedCount + viewedDelta,
                submittedCount = stats.submittedCount + submittedDelta,
              )
            saveStats(prefs, stats)
          },
        )
      }
    }
  }
}

@Composable
private fun AuthGateScreen(onContinue: () -> Unit) {
  val placeholderPaths =
    listOf(
      "android/app/google-services.json",
      "android/app/src/main/java/com/hawkfranklin/aura/hawkfranklin/HawkFranklinConfig.kt",
      "backend/service-account.json",
    )

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFFFFFCF6), Color(0xFFF4F1E7), Color(0xFFFFFFFF))
          )
        )
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(24.dp),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Image(
          painter = painterResource(R.drawable.hawkfranklin_logo),
          contentDescription = "HawkFranklin logo",
          modifier = Modifier.size(56.dp),
        )
        Column {
          Text(
            "HawkFranklin",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
          )
          Text(
            "Internal research collection app",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
          )
        }
      }

      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            "Clinical contribution, structured like an internal trial feed.",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
          )
          Text(
            "Physicians and clinicians sign in, join research projects, and answer flash-card style tasks built from open datasets treated like live review queues.",
            style = MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            textAlign = TextAlign.Center,
          )
          Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AuthChip("Google auth gate")
                AuthChip("One-time onboarding")
              }
              AuthChip("Project panels")
            }
          }
        }
      }

      /*
      Card(
        shape = RoundedCornerShape(24.dp),
        colors =
          CardDefaults.cardColors(containerColor = Color(0xFFFFF8E8)),
        border = BorderStroke(1.dp, Color(0xFFE6D6A8)),
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            "Firebase placeholders",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          )
          Text(
            if (HawkFranklinConfig.isFirebaseConfigured()) {
              "Firebase config values have been replaced."
            } else {
              "This starter app is wired for placeholder mode until Firebase and backend secrets are dropped in."
            },
            style = MaterialTheme.typography.bodyMedium,
          )
          placeholderPaths.forEach { path ->
            Text(
              path,
              style =
                MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = FontWeight.SemiBold,
                ),
            )
          }
        }
      }
      */
    }

    Button(
      onClick = onContinue,
      modifier = Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 12.dp),
      shape = RoundedCornerShape(18.dp),
      colors =
        ButtonDefaults.buttonColors(
          containerColor = Color(0xFF1F2937),
          contentColor = Color.White,
        ),
    ) {
      Icon(Icons.Outlined.VerifiedUser, contentDescription = null)
      Spacer(modifier = Modifier.width(10.dp))
      Text("Continue with Google placeholder")
    }
  }
}

@Composable
private fun AuthChip(label: String) {
  Box(
    modifier =
      Modifier
        .background(Color(0xFFF2EEE3), RoundedCornerShape(999.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp),
  ) {
    Text(
      label,
      style =
        MaterialTheme.typography.labelMedium.copy(
          color = Color(0xFF4B5563),
          fontWeight = FontWeight.Bold,
        ),
    )
  }
}

@Composable
private fun OnboardingScreen(
  initialProfile: ClinicianProfile?,
  onBack: () -> Unit,
  onComplete: (ClinicianProfile) -> Unit,
) {
  var name by remember { mutableStateOf(initialProfile?.name ?: "") }
  var email by remember { mutableStateOf(initialProfile?.email ?: "") }
  var institution by remember { mutableStateOf(initialProfile?.institution ?: "") }
  var role by remember { mutableStateOf(initialProfile?.role ?: "Physician") }
  var accepted by remember { mutableStateOf(false) }
  val roles = listOf("Physician", "Clinician", "Resident", "Research Fellow")

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
    IconButton(
      onClick = onBack,
      modifier =
        Modifier
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
      Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }

    Text(
      "One-time onboarding",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Center,
    )
    Text(
      "This profile is the placeholder that later maps to Firebase Auth plus Firestore user records.",
      style = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Center,
    )

    OutlinedTextField(
      value = name,
      onValueChange = { name = it },
      label = { Text("Full name") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      shape = RoundedCornerShape(18.dp),
    )
    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("Company or research email") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      shape = RoundedCornerShape(18.dp),
    )
    OutlinedTextField(
      value = institution,
      onValueChange = { institution = it },
      label = { Text("Institution or department") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      shape = RoundedCornerShape(18.dp),
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text(
        "Clinical role",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      )
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        roles.forEach { current ->
          FilterChip(
            selected = role == current,
            onClick = { role = current },
            label = { Text(current) },
          )
        }
      }
    }

    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            "I understand this is an internal research pilot",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
          )
          Text(
            "Answers in this starter build are stored locally until backend collection is wired.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = accepted, onCheckedChange = { accepted = it })
      }
    }

    Button(
      onClick = {
        onComplete(
          ClinicianProfile(
            name = name.trim(),
            email = email.trim(),
            role = role,
            institution = institution.trim(),
          )
        )
      },
      enabled =
        name.isNotBlank() &&
          email.isNotBlank() &&
          institution.isNotBlank() &&
          accepted,
      modifier = Modifier.fillMaxWidth().height(56.dp),
      shape = RoundedCornerShape(18.dp),
    ) {
      Text("Save profile and open research feed")
    }
  }
}

@Composable
private fun DashboardScreen(
  profile: ClinicianProfile?,
  stats: ResearchStats,
  projects: List<DemoProject>,
  onOpenProject: (DemoProject) -> Unit,
  onSwitchUser: () -> Unit,
) {
  LazyColumn(
    modifier =
      Modifier
        .fillMaxSize()
        .statusBarsPadding(),
    contentPadding = PaddingValues(24.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(R.drawable.hawkfranklin_logo),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
          )
          Column {
            Text(
              "HawkFranklin",
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            )
            Text(
              profile?.let { "${it.name} • ${it.role}" } ?: "Research contributor",
              style =
                MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
          }
        }
        Button(
          onClick = onSwitchUser,
          shape = RoundedCornerShape(14.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
              contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
          Text("Auth")
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = Color(0xFFF6F7FB),
          ),
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            "Ongoing projects",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
          )
          Text(
            "Panels below represent internal data-collection streams. The number on each card is the remaining sample count needed before inclusion in the current paper cohort.",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
          )
        }
      }
    }

    item {
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatsCard(
          title = "Cases viewed",
          value = stats.viewedCount.toString(),
          accent = Color(0xFF1D4ED8),
          modifier = Modifier.weight(1f),
        )
        StatsCard(
          title = "Labels submitted",
          value = stats.submittedCount.toString(),
          accent = Color(0xFF047857),
          modifier = Modifier.weight(1f),
        )
      }
    }

    items(projects) { project ->
      ProjectCard(project = project, onClick = { onOpenProject(project) })
    }
  }
}

@Composable
private fun StatsCard(
  title: String,
  value: String,
  accent: Color,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE6E8EF)),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(34.dp)
            .background(accent.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(Icons.Outlined.Person, contentDescription = null, tint = accent)
      }
      Text(
        value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
      )
      Text(
        title,
        style =
          MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
      )
    }
  }
}

@Composable
private fun ProjectCard(project: DemoProject, onClick: () -> Unit) {
  Card(
    onClick = onClick,
    enabled = project.demoReady,
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE6E8EF)),
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            project.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
          )
          Text(
            project.summary,
            style =
              MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Box(
          modifier =
            Modifier
              .clip(RoundedCornerShape(18.dp))
              .background(project.accent.copy(alpha = 0.12f))
              .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              project.remainingForPaper.toString(),
              style =
                MaterialTheme.typography.titleLarge.copy(
                  color = project.accent,
                  fontWeight = FontWeight.Black,
                ),
            )
            Text(
              "remaining",
              style =
                MaterialTheme.typography.labelSmall.copy(
                  color = project.accent,
                  fontWeight = FontWeight.Bold,
                ),
            )
          }
        }
      }

      LinearProgressIndicator(
        progress = {
          ((project.totalForPaper - project.remainingForPaper).toFloat() / project.totalForPaper)
            .coerceIn(0f, 1f)
        },
        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)),
        color = project.accent,
        trackColor = Color(0xFFF1F2F6),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(
            if (project.demoReady) Icons.Outlined.Science else Icons.Outlined.Folder,
            contentDescription = null,
            tint = project.accent,
          )
          Text(
            if (project.demoReady) "Demo task live" else "Backend collection pending",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
          )
        }
        if (project.demoReady) {
          Icon(Icons.Outlined.ArrowOutward, contentDescription = null, tint = project.accent)
        }
      }
    }
  }
}

@Composable
private fun ProjectFlashcardScreen(
  project: DemoProject,
  cards: List<FlashcardCase>,
  stats: ResearchStats,
  onBack: () -> Unit,
  onStatsChanged: (viewedDelta: Int, submittedDelta: Int) -> Unit,
) {
  val pagerState = rememberPagerState(pageCount = { cards.size })
  val viewedIds = remember { mutableStateMapOf<String, Boolean>() }
  val submittedIds = remember { mutableStateMapOf<String, String>() }

  LaunchedEffect(pagerState.currentPage) {
    val currentCard = cards[pagerState.currentPage]
    if (viewedIds[currentCard.id] != true) {
      viewedIds[currentCard.id] = true
      onStatsChanged(1, 0)
    }
  }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F7F8))
        .statusBarsPadding()
        .navigationBarsPadding(),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
        }
        Column {
          Text(
            project.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
          )
          Text(
            "Swipe vertically like a review reel",
            style =
              MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )
        }
      }
      Box(
        modifier =
          Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE7E9F0), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
      ) {
        Text(
          "${stats.submittedCount} submissions",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
      }
    }

    VerticalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize(),
      beyondViewportPageCount = 1,
      contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
      pageSpacing = 16.dp,
    ) { page ->
      val card = cards[page]
      val selectedOption = submittedIds[card.id]

      Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8EAF1)),
        modifier = Modifier.fillMaxSize(),
      ) {
        Column(
          modifier =
            Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                "Card ${page + 1} of ${cards.size}",
                style =
                  MaterialTheme.typography.labelLarge.copy(
                    color = project.accent,
                    fontWeight = FontWeight.Bold,
                  ),
              )
              Text(
                card.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
              )
            }
            Box(
              modifier =
                Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0xFFF5F6FB))
                  .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
              Text(
                if (selectedOption == null) "Awaiting answer" else "Recorded",
                style =
                  MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color =
                      if (selectedOption == null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                      } else {
                        Color(0xFF047857)
                      },
                  ),
              )
            }
          }

          FlashcardImageStrip(images = card.images)

          Text(
            card.prompt,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          )
          Text(
            "Choose the answer that best matches your clinical reading. This demo stores the selection locally as a placeholder for future Firebase collection.",
            style =
              MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            card.options.forEach { option ->
              val isSelected = selectedOption == option
              Button(
                onClick = {
                  if (selectedOption != null) {
                    return@Button
                  }
                  submittedIds[card.id] = option
                  onStatsChanged(0, 1)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor =
                      if (isSelected) {
                        project.accent
                      } else {
                        Color(0xFFF7F8FC)
                      },
                    contentColor =
                      if (isSelected) {
                        Color.White
                      } else {
                        Color(0xFF111827)
                      },
                  ),
                border =
                  if (isSelected) {
                    null
                  } else {
                    BorderStroke(1.dp, Color(0xFFE5E7EF))
                  },
              ) {
                Text(
                  option,
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
              }
            }
          }

          Spacer(modifier = Modifier.weight(1f))
          Text(
            if (selectedOption == null) {
              "Swipe for the next flashcard when ready."
            } else {
              "Selection saved for this card. Swipe to continue."
            },
            style =
              MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )
        }
      }
    }
  }
}

@Composable
private fun FlashcardImageStrip(@DrawableRes images: List<Int>) {
  if (images.size == 1) {
    Image(
      painter = painterResource(images.first()),
      contentDescription = null,
      modifier =
        Modifier
          .fillMaxWidth()
          .aspectRatio(1.15f)
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFFF4F5F9)),
      contentScale = ContentScale.Crop,
    )
    return
  }

  Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
    images.forEach { image ->
      Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier =
          Modifier
            .weight(1f)
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF4F5F9)),
        contentScale = ContentScale.Crop,
      )
    }
  }
}

private fun buildProjects(submittedCount: Int): List<DemoProject> {
  val total = 120
  val remaining = (total - submittedCount).coerceAtLeast(0)
  return listOf(
    DemoProject(
      id = "derm_flashcards",
      name = "Dermatology Flash Guard",
      summary =
        "Image-led flashcards where physicians classify visible findings from one or more lesion photos.",
      remainingForPaper = remaining,
      totalForPaper = total,
      demoReady = true,
      accent = Color(0xFF0F766E),
    ),
    DemoProject(
      id = "lesion_strategy",
      name = "Lesion Strategy Notes",
      summary =
        "Coming next: short text cases where clinicians propose investigation or management strategy.",
      remainingForPaper = 84,
      totalForPaper = 84,
      demoReady = false,
      accent = Color(0xFFB45309),
    ),
    DemoProject(
      id = "paper_language",
      name = "Paper Consensus Language",
      summary =
        "Coming next: abstract and caption review tasks to harmonize language for collaborative manuscripts.",
      remainingForPaper = 42,
      totalForPaper = 42,
      demoReady = false,
      accent = Color(0xFF4338CA),
    ),
  )
}

private fun buildDemoCards(): List<FlashcardCase> {
  return listOf(
    FlashcardCase(
      id = "eczema_pair",
      title = "Dermatology Demo 01",
      prompt = "Which option best matches the dominant pattern shown across these two images?",
      images = listOf(R.drawable.eczema_1, R.drawable.eczema_2),
      options = listOf("Eczema", "Psoriasis", "Tinea", "Herpes zoster"),
    ),
    FlashcardCase(
      id = "psoriasis_pair",
      title = "Dermatology Demo 02",
      prompt = "Select the most likely diagnosis based on plaque appearance and scaling.",
      images = listOf(R.drawable.psoriasis_1, R.drawable.psoriasis_2),
      options = listOf("Psoriasis", "Eczema", "Tinea", "Drug rash"),
    ),
    FlashcardCase(
      id = "tinea_single",
      title = "Dermatology Demo 03",
      prompt = "What do you see in this case?",
      images = listOf(R.drawable.tinea_1),
      options = listOf("Tinea", "Eczema", "Psoriasis", "Impetigo"),
    ),
    FlashcardCase(
      id = "herpes_pair",
      title = "Dermatology Demo 04",
      prompt = "Which answer best describes this vesicular presentation?",
      images = listOf(R.drawable.herpes_zoster_1, R.drawable.herpes_zoster_2),
      options = listOf("Herpes zoster", "Drug rash", "Psoriasis", "Folliculitis"),
    ),
    FlashcardCase(
      id = "tinea_pair",
      title = "Dermatology Demo 05",
      prompt = "Select the best match for this paired review set.",
      images = listOf(R.drawable.tinea_1, R.drawable.tinea_2),
      options = listOf("Tinea", "Allergic contact dermatitis", "Urticaria", "Eczema"),
    ),
    FlashcardCase(
      id = "eczema_single",
      title = "Dermatology Demo 06",
      prompt = "For this single-image card, what is your top answer?",
      images = listOf(R.drawable.eczema_2),
      options = listOf("Eczema", "Tinea", "Psoriasis", "Photodermatitis"),
    ),
  )
}

private fun loadProfile(prefs: SharedPreferences): ClinicianProfile? {
  if (!prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)) {
    return null
  }
  return ClinicianProfile(
    name = prefs.getString(KEY_NAME, "") ?: "",
    email = prefs.getString(KEY_EMAIL, "") ?: "",
    role = prefs.getString(KEY_ROLE, "Physician") ?: "Physician",
    institution = prefs.getString(KEY_INSTITUTION, "") ?: "",
  )
}

private fun saveProfile(prefs: SharedPreferences, profile: ClinicianProfile) {
  prefs
    .edit()
    .putBoolean(KEY_ONBOARDING_COMPLETE, true)
    .putString(KEY_NAME, profile.name)
    .putString(KEY_EMAIL, profile.email)
    .putString(KEY_ROLE, profile.role)
    .putString(KEY_INSTITUTION, profile.institution)
    .apply()
}

private fun loadStats(prefs: SharedPreferences): ResearchStats {
  return ResearchStats(
    viewedCount = prefs.getInt(KEY_VIEWED, 0),
    submittedCount = prefs.getInt(KEY_SUBMITTED, 0),
  )
}

private fun saveStats(prefs: SharedPreferences, stats: ResearchStats) {
  prefs
    .edit()
    .putInt(KEY_VIEWED, stats.viewedCount)
    .putInt(KEY_SUBMITTED, stats.submittedCount)
    .apply()
}
