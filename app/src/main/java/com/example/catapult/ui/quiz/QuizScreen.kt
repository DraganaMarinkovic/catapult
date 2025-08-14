package com.example.catapult.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.catapult.viewmodel.quiz.QuizViewModel
import com.example.catapult.db.QuizType
import com.example.catapult.data.model.ImageData
import com.example.catapult.db.toImageData
import com.example.catapult.viewmodel.quiz.QuizEvent
import com.example.catapult.viewmodel.quiz.QuizSideEffect
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    navigateToLeaderboard: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val ctx = LocalContext.current

    var showDialog by rememberSaveable { mutableStateOf(false) } //
    var showExitDialog by rememberSaveable { mutableStateOf(false) } //

    var finalScore by remember { mutableStateOf(0f) } //

    BackHandler {
        viewModel.sendEvent(QuizEvent.ExitQuiz)
    }

    var imageData: ImageData by remember { mutableStateOf(ImageData(id = "", url = "", width = 0, height = 0)) }

    fun setImageData(image: ImageData?){
        if (image != null) {
            imageData = image
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { effect ->
            when (effect) {
                is QuizSideEffect.ShowScoreDialog -> {
                    showDialog = true
                    finalScore = effect.finalScore
                }
                is QuizSideEffect.NavigateToLeaderboard -> {
                    navigateToLeaderboard()
                }
                is QuizSideEffect.ShowExitConfirmation -> showExitDialog = true
                is QuizSideEffect.NavigateBack -> navigateToLeaderboard()
                is QuizSideEffect.SendImage -> setImageData(effect.image)
            }
        }
    }

    Scaffold (
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    TopAppBar(
                        title = { Text("Quiz") },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.sendEvent(QuizEvent.ExitQuiz) }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        },

        ){ padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinearProgressIndicator(
                progress = (state.elapsedTime / 300f).coerceIn(0f,1f),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Time left: ${300 - state.elapsedTime}s")

            Spacer(Modifier.height(8.dp))

            Text(
                "Question ${state.currentIndex + 1} / ${state.questions.size}",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.height(8.dp))

            if (state.questions.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading questions…")
                }
            } else {
                val q = state.questions[state.currentIndex]

                viewModel.sendEvent(QuizEvent.RequestImage(q.imageId)) //

                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(imageData!!.url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    when (q.type) {
                        QuizType.GUESS_BREED -> "Which breed is this?"
                        QuizType.FIND_INTRUDER -> "Which trait does NOT belong?"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                q.options.forEach { option ->
                    Button(
                        onClick = {
                            viewModel.sendEvent(QuizEvent.SubmitAnswer(
                                state.currentIndex,
                                option
                            ))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(option)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            if (showDialog) {
                ScoreDialog(finalScoreToShow = finalScore.toInt(),
                    onButtonClick = {
                        viewModel.sendEvent(QuizEvent.NavigateToLeaderboard)
                        showDialog = false
                    },
                    onSubmitClick = {
                        viewModel.sendEvent(QuizEvent.SubmitResult)
                    })
            }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Exit Quiz?") },
                    text = { Text("If you exit now, your progress will be lost.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            viewModel.sendEvent(QuizEvent.ConfirmExit)
                        }) {
                            Text("Yes, exit")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("No, continue")
                        }
                    },
                    titleContentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ScoreDialog(finalScoreToShow: Int, onButtonClick: () -> Unit, onSubmitClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onButtonClick },
        title = {
            Text(text = "Quiz Completed!")
        },
        text = {
            Text("Your final score is ${finalScoreToShow.toInt()}")
        },
        confirmButton = {
            TextButton(onClick = {
                onButtonClick()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onSubmitClick()
                onButtonClick()
            }) { Text("Upload Result")}
        },
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant

    )
}
