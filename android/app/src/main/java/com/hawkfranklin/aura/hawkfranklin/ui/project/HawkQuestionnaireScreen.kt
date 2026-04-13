package com.hawkfranklin.aura.hawkfranklin.ui.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hawkfranklin.aura.hawkfranklin.model.ProjectTile
import com.hawkfranklin.aura.hawkfranklin.model.QuestionType
import com.hawkfranklin.aura.hawkfranklin.model.QuestionnaireAnswer
import com.hawkfranklin.aura.hawkfranklin.model.QuestionnaireQuestion

@Composable
fun HawkQuestionnaireScreen(
  project: ProjectTile,
  questions: List<QuestionnaireQuestion>,
  syncMessage: String?,
  onBack: () -> Unit,
  onSubmit: (List<QuestionnaireAnswer>) -> Unit,
) {
  var index by remember { mutableStateOf(0) }
  val answers = remember { mutableStateMapOf<String, String>() }
  val reviewMode = index >= questions.size
  val progress = if (questions.isEmpty()) 0f else (index.toFloat() / questions.size.toFloat()).coerceIn(0f, 1f)

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(Color(0xFFF8F8FA))
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
          Text(project.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
          Text(
            if (reviewMode) "Review and submit" else "Question ${index + 1} of ${questions.size}",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
          )
        }
      }
      Box(
        modifier =
          Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
      ) {
        Text("${answers.count { it.value.isNotBlank() }} answered")
      }
    }

    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
      trackColor = Color(0xFFE7E7EF),
    )

    if (reviewMode) {
      ReviewScreen(
        questions = questions,
        answers = answers,
        syncMessage = syncMessage,
        onBack = { index = questions.lastIndex },
        onSubmit = {
          onSubmit(
            questions.map {
              QuestionnaireAnswer(
                questionId = it.id,
                title = it.title,
                value = answers[it.id].orEmpty(),
              )
            }
          )
        },
      )
    } else {
      val question = questions[index]
      QuestionCard(
        question = question,
        value = answers[question.id].orEmpty(),
        syncMessage = syncMessage,
        onValueChange = { answers[question.id] = it },
        onPrevious = { if (index > 0) index -= 1 },
        onNext = {
          if (index < questions.lastIndex) {
            index += 1
          } else {
            index = questions.size
          }
        },
        canGoBack = index > 0,
      )
    }
  }
}

@Composable
private fun QuestionCard(
  question: QuestionnaireQuestion,
  value: String,
  syncMessage: String?,
  onValueChange: (String) -> Unit,
  onPrevious: () -> Unit,
  onNext: () -> Unit,
  canGoBack: Boolean,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(18.dp)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE4E7EC)),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Text(question.title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black))
        Text(question.prompt, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(
          question.helper,
          style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        )

        if (question.images.isNotEmpty()) {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            question.images.forEach { image ->
              Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.weight(1f).aspectRatio(0.9f).clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop,
              )
            }
          }
        }

        when (question.type) {
          QuestionType.MultipleChoice ->
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              question.options.forEach { option ->
                Button(
                  onClick = { onValueChange(option) },
                  modifier = Modifier.fillMaxWidth().height(56.dp),
                  shape = RoundedCornerShape(18.dp),
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = if (value == option) Color(0xFF111827) else Color(0xFFF4F6F8),
                      contentColor = if (value == option) Color.White else Color(0xFF111827),
                    ),
                ) {
                  Text(option, textAlign = TextAlign.Center)
                }
              }
            }

          QuestionType.Numeric ->
            OutlinedTextField(
              value = value,
              onValueChange = onValueChange,
              label = { Text("Numeric answer") },
              modifier = Modifier.fillMaxWidth(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              placeholder = { Text(question.placeholder) },
              singleLine = true,
            )

          QuestionType.Text ->
            OutlinedTextField(
              value = value,
              onValueChange = onValueChange,
              label = { Text("Written answer") },
              modifier = Modifier.fillMaxWidth().height(180.dp),
              placeholder = { Text(question.placeholder) },
            )
        }

        if (syncMessage != null) {
          Text(
            syncMessage,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309)),
          )
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
      if (canGoBack) {
        Button(
          onClick = onPrevious,
          modifier = Modifier.weight(1f).height(54.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = Color(0xFFEFF1F5),
              contentColor = Color(0xFF111827),
            ),
        ) {
          Text("Previous")
        }
      }
      Button(
        onClick = onNext,
        modifier = Modifier.weight(1f).height(54.dp),
        enabled = value.isNotBlank(),
      ) {
        Text("Next")
      }
    }
  }
}

@Composable
private fun ReviewScreen(
  questions: List<QuestionnaireQuestion>,
  answers: Map<String, String>,
  syncMessage: String?,
  onBack: () -> Unit,
  onSubmit: () -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(18.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text("Review answers", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black))
          Text(
            "Check each answer before submission. This is the final step before the session is written to Firebase.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
          )
          if (syncMessage != null) {
            Text(
              syncMessage,
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309)),
            )
          }
        }
      }
    }

    items(questions.size) { index ->
      val question = questions[index]
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(question.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text(
            answers[question.id].orEmpty().ifBlank { "No answer entered" },
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }

    item {
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = onBack,
          modifier = Modifier.weight(1f).height(54.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = Color(0xFFEFF1F5),
              contentColor = Color(0xFF111827),
            ),
        ) {
          Text("Back")
        }
        Button(
          onClick = onSubmit,
          modifier = Modifier.weight(1f).height(54.dp),
        ) {
          Text("Submit")
        }
      }
    }
  }
}
