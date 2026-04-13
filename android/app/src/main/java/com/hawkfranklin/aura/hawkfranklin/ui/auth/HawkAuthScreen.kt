package com.hawkfranklin.aura.hawkfranklin.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hawkfranklin.aura.R

private enum class AuthMode {
  SignIn,
  Register,
  Reset,
}

@Composable
fun HawkAuthScreen(onBack: () -> Unit) {
  val context = LocalContext.current
  val activity = context as? Activity
  val auth = remember { FirebaseAuth.getInstance() }
  var mode by remember { mutableStateOf(AuthMode.SignIn) }
  var email by remember { mutableStateOf(auth.currentUser?.email.orEmpty()) }
  var password by remember { mutableStateOf("") }
  var status by remember { mutableStateOf<String?>(null) }
  var loading by remember { mutableStateOf(false) }

  val webClientId = remember {
    val resourceId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    if (resourceId == 0) null else context.getString(resourceId)
  }
  val googleSignInClient =
    remember(webClientId) {
      if (webClientId.isNullOrBlank()) {
        null
      } else {
        GoogleSignIn.getClient(
          context,
          GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build(),
        )
      }
    }

  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode != Activity.RESULT_OK) {
        loading = false
        status = "Google sign-in was canceled."
        googleSignInClient?.signOut()
        return@rememberLauncherForActivityResult
      }
      val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
      try {
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken
        if (idToken.isNullOrBlank()) {
          loading = false
          status = "Google sign-in did not return an ID token."
          return@rememberLauncherForActivityResult
        }
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
          .addOnSuccessListener {
            loading = false
            status = "Google account connected."
          }
          .addOnFailureListener { error ->
            loading = false
            status = error.localizedMessage ?: "Google authentication failed."
            googleSignInClient?.signOut()
          }
      } catch (error: ApiException) {
        loading = false
        status = error.localizedMessage ?: "Google authentication failed."
        googleSignInClient?.signOut()
      }
    }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFFFFFCF6), Color(0xFFF4EBDD), Color(0xFFFFFFFF))
          )
        )
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    IconButton(onClick = onBack) {
      Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }

    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
      Image(
        painter = painterResource(R.drawable.hawkfranklin_logo),
        contentDescription = null,
        modifier = Modifier.size(56.dp),
      )
      Column {
        Text(
          "Login to HawkFranklin",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
        )
        Text(
          "Single clinician account flow with Firebase Auth and sync-ready project state.",
          style =
            MaterialTheme.typography.bodyMedium.copy(
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
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          AuthModeButton(
            label = "Sign in",
            selected = mode == AuthMode.SignIn,
            onClick = { mode = AuthMode.SignIn },
          )
          AuthModeButton(
            label = "Register",
            selected = mode == AuthMode.Register,
            onClick = { mode = AuthMode.Register },
          )
          AuthModeButton(
            label = "Reset",
            selected = mode == AuthMode.Reset,
            onClick = { mode = AuthMode.Reset },
          )
        }

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email address") },
          modifier = Modifier.fillMaxWidth(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          shape = RoundedCornerShape(18.dp),
        )

        if (mode != AuthMode.Reset) {
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
          )
        }

        if (status != null) {
          Text(
            status ?: "",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF9A3412)),
          )
        }

        when (mode) {
          AuthMode.SignIn ->
            Button(
              onClick = {
                if (email.isBlank() || password.isBlank()) {
                  status = "Enter both email and password to continue."
                  return@Button
                }
                loading = true
                status = null
                auth.signInWithEmailAndPassword(email.trim(), password)
                  .addOnSuccessListener {
                    loading = false
                    status = "Signed in successfully."
                  }
                  .addOnFailureListener { error ->
                    loading = false
                    status = error.localizedMessage ?: "Sign-in failed."
                  }
              },
              modifier = Modifier.fillMaxWidth().height(56.dp),
              enabled = !loading,
            ) {
              Icon(Icons.Outlined.VerifiedUser, contentDescription = null)
              Spacer(modifier = Modifier.size(10.dp))
              Text(if (loading) "Signing in..." else "Sign in")
            }

          AuthMode.Register ->
            Button(
              onClick = {
                if (email.isBlank() || password.length < 6) {
                  status = "Registration requires an email and a password of at least 6 characters."
                  return@Button
                }
                loading = true
                status = null
                auth.createUserWithEmailAndPassword(email.trim(), password)
                  .addOnSuccessListener {
                    loading = false
                    status = "Account created. Complete onboarding next."
                  }
                  .addOnFailureListener { error ->
                    loading = false
                    status = error.localizedMessage ?: "Registration failed."
                  }
              },
              modifier = Modifier.fillMaxWidth().height(56.dp),
              enabled = !loading,
            ) {
              Icon(Icons.Outlined.PersonAdd, contentDescription = null)
              Spacer(modifier = Modifier.size(10.dp))
              Text(if (loading) "Creating account..." else "Create account")
            }

          AuthMode.Reset ->
            Button(
              onClick = {
                if (email.isBlank()) {
                  status = "Enter the email address for the reset link."
                  return@Button
                }
                loading = true
                status = null
                auth.sendPasswordResetEmail(email.trim())
                  .addOnSuccessListener {
                    loading = false
                    status = "Password reset email sent."
                  }
                  .addOnFailureListener { error ->
                    loading = false
                    status = error.localizedMessage ?: "Password reset failed."
                  }
              },
              modifier = Modifier.fillMaxWidth().height(56.dp),
              enabled = !loading,
            ) {
              Icon(Icons.Outlined.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.size(10.dp))
              Text(if (loading) "Sending reset..." else "Send reset email")
            }
        }

        Button(
          onClick = {
            if (googleSignInClient == null || activity == null) {
              status = "Google sign-in is not ready until the Firebase client is fully configured."
              return@Button
            }
            loading = true
            status = null
            launcher.launch(googleSignInClient.signInIntent)
          },
          modifier = Modifier.fillMaxWidth().height(54.dp),
          enabled = !loading,
          colors =
            ButtonDefaults.buttonColors(
              containerColor = Color(0xFF111827),
              contentColor = Color.White,
            ),
          shape = RoundedCornerShape(18.dp),
        ) {
          Icon(Icons.Outlined.Mail, contentDescription = null)
          Spacer(modifier = Modifier.size(10.dp))
          Text("Continue with Google")
        }
      }
    }
  }
}

@Composable
private fun AuthModeButton(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(999.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = if (selected) Color(0xFF111827) else Color(0xFFF3F4F6),
        contentColor = if (selected) Color.White else Color(0xFF111827),
      ),
  ) {
    Text(label)
  }
}
