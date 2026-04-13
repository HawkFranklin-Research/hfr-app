package com.hawkfranklin.aura.hawkfranklin

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.hawkfranklin.aura.hawkfranklin.data.HawkFranklinRepository
import com.hawkfranklin.aura.hawkfranklin.model.ClinicianProfile
import com.hawkfranklin.aura.hawkfranklin.model.HomeMetrics
import com.hawkfranklin.aura.hawkfranklin.model.ProjectTile
import com.hawkfranklin.aura.hawkfranklin.ui.auth.HawkAuthScreen
import com.hawkfranklin.aura.hawkfranklin.ui.home.HawkDrawerContent
import com.hawkfranklin.aura.hawkfranklin.ui.home.HawkHomeScreen
import com.hawkfranklin.aura.hawkfranklin.ui.onboarding.HawkOnboardingScreen
import com.hawkfranklin.aura.hawkfranklin.ui.project.HawkConsentDialog
import com.hawkfranklin.aura.hawkfranklin.ui.project.HawkQuestionnaireScreen
import com.hawkfranklin.aura.hawkfranklin.ui.settings.HawkSettingsScreen
import kotlinx.coroutines.launch

private enum class HawkRoute {
  LANDING,
  HOME,
  AUTH,
  ONBOARDING,
  QUESTIONNAIRE,
  SETTINGS,
}

@Composable
fun HawkFranklinApp() {
  val repository = remember { HawkFranklinRepository() }
  val seededProjects = remember(repository) { repository.defaultProjects() }
  val auth = remember { FirebaseAuth.getInstance() }
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  var route by remember { mutableStateOf(HawkRoute.LANDING) }
  var firebaseUser by remember { mutableStateOf(auth.currentUser) }
  var profile by remember { mutableStateOf<ClinicianProfile?>(null) }
  var metrics by remember { mutableStateOf(HomeMetrics()) }
  var projects by remember { mutableStateOf(seededProjects) }
  var projectsLoading by remember { mutableStateOf(false) }
  var selectedProject by remember { mutableStateOf<ProjectTile?>(null) }
  var pendingProject by remember { mutableStateOf<ProjectTile?>(null) }
  var consentProject by remember { mutableStateOf<ProjectTile?>(null) }
  var syncMessage by remember { mutableStateOf<String?>(null) }
  var profileReadyKey by remember { mutableStateOf<String?>(null) }

  fun openQuestionnaire(project: ProjectTile) {
    val activeUser = firebaseUser
    val activeProfile = profile
    if (activeUser == null) {
      pendingProject = project
      route = HawkRoute.AUTH
      return
    }
    if (activeProfile == null || !activeProfile.onboardingComplete) {
      pendingProject = project
      route = HawkRoute.ONBOARDING
      return
    }
    repository.recordConsent(activeUser.uid, project) { warning ->
      syncMessage = warning
      selectedProject = project
      route = HawkRoute.QUESTIONNAIRE
    }
  }

  LaunchedEffect(Unit) {
    repository.loadProjects {
      projects = it
      projectsLoading = false
    }
  }

  DisposableEffect(auth) {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      firebaseUser = firebaseAuth.currentUser
    }
    auth.addAuthStateListener(listener)
    onDispose { auth.removeAuthStateListener(listener) }
  }

  LaunchedEffect(firebaseUser?.uid) {
    val activeUser = firebaseUser
    if (activeUser == null) {
      profile = null
      metrics = HomeMetrics()
      profileReadyKey = null
      if (route != HawkRoute.AUTH) {
        route = HawkRoute.LANDING
      }
      return@LaunchedEffect
    }

    repository.ensureUserShell(activeUser) { loadedProfile ->
      profile = loadedProfile
      profileReadyKey = "${loadedProfile.uid}:${loadedProfile.onboardingComplete}"
      repository.loadMetrics(activeUser.uid) { loadedMetrics -> metrics = loadedMetrics }
      route =
        if (loadedProfile.onboardingComplete) {
          if (route == HawkRoute.SETTINGS) HawkRoute.SETTINGS else HawkRoute.HOME
        } else {
          HawkRoute.ONBOARDING
        }
    }
  }

  LaunchedEffect(profileReadyKey, pendingProject?.id) {
    if (profile?.onboardingComplete == true && pendingProject != null) {
      val project = pendingProject ?: return@LaunchedEffect
      pendingProject = null
      openQuestionnaire(project)
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = route == HawkRoute.LANDING || route == HawkRoute.HOME,
    drawerContent = {
      HawkDrawerContent(
        firebaseUser = firebaseUser,
        profile = profile,
        onLogin = {
          route = HawkRoute.AUTH
          scope.launch { drawerState.close() }
        },
        onSettings = {
          route = HawkRoute.SETTINGS
          scope.launch { drawerState.close() }
        },
        onCompleteProfile = {
          route = HawkRoute.ONBOARDING
          scope.launch { drawerState.close() }
        },
        onLogout = {
          auth.signOut()
          selectedProject = null
          pendingProject = null
          route = HawkRoute.LANDING
          scope.launch { drawerState.close() }
        },
      )
    },
  ) {
    Surface(modifier = Modifier) {
      when (route) {
        HawkRoute.AUTH ->
          HawkAuthScreen(
            onBack = { route = if (firebaseUser == null) HawkRoute.LANDING else HawkRoute.HOME }
          )

        HawkRoute.ONBOARDING -> {
          val activeUser = firebaseUser
          if (activeUser != null) {
            HawkOnboardingScreen(
              firebaseUser = activeUser,
              existingProfile = profile,
              onBack = { route = if (firebaseUser == null) HawkRoute.LANDING else HawkRoute.HOME },
              onSave = { displayName, institution, specialty ->
                repository.saveProfile(
                  ClinicianProfile(
                    uid = activeUser.uid,
                    displayName = displayName,
                    email = activeUser.email.orEmpty(),
                    institution = institution,
                    specialty = specialty,
                    onboardingComplete = true,
                  ),
                  onSuccess = { saved ->
                    profile = saved
                    profileReadyKey = "${saved.uid}:${saved.onboardingComplete}:${pendingProject?.id ?: "none"}"
                    route = HawkRoute.HOME
                  },
                  onFailure = { error -> syncMessage = error },
                )
              },
            )
          }
        }

        HawkRoute.QUESTIONNAIRE -> {
          val project = selectedProject
          val activeUser = firebaseUser
          val activeProfile = profile
          if (project != null && activeUser != null && activeProfile != null) {
            HawkQuestionnaireScreen(
              project = project,
              questions = repository.questionnaireFor(project.id),
              syncMessage = syncMessage,
              onBack = { route = HawkRoute.HOME },
              onSubmit = { answers ->
                repository.submitQuestionnaire(
                  uid = activeUser.uid,
                  profile = activeProfile,
                  project = project,
                  answers = answers,
                ) { warning ->
                  syncMessage =
                    warning ?: "Questionnaire submitted for ${project.name}."
                  repository.loadMetrics(activeUser.uid) { loadedMetrics -> metrics = loadedMetrics }
                  selectedProject = null
                  route = HawkRoute.HOME
                }
              },
            )
          }
        }

        HawkRoute.SETTINGS ->
          HawkSettingsScreen(
            firebaseUser = firebaseUser,
            profile = profile,
            syncMessage = syncMessage,
            onBack = { route = if (firebaseUser == null) HawkRoute.LANDING else HawkRoute.HOME },
            onLogout = {
              auth.signOut()
              route = HawkRoute.LANDING
            },
          )

        HawkRoute.LANDING,
        HawkRoute.HOME ->
          HawkHomeScreen(
            authenticated = firebaseUser != null,
            profile = profile,
            metrics = metrics,
            projects = projects,
            projectsLoading = projectsLoading,
            syncMessage = syncMessage,
            onProfileClick = { scope.launch { drawerState.open() } },
            onProjectClick = { consentProject = it },
          )
      }

      consentProject?.let { project ->
        HawkConsentDialog(
          project = project,
          onDismiss = { consentProject = null },
          onAgree = {
            consentProject = null
            openQuestionnaire(project)
          },
        )
      }
    }
  }
}
