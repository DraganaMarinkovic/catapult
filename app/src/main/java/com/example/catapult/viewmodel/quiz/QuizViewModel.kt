package com.example.catapult.viewmodel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.db.ImageEntity
import com.example.catapult.db.toImageData
import com.example.catapult.repository.AccountRepository
import com.example.catapult.repository.LeaderboardRepository
import com.example.catapult.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: QuizRepository,
    private val accountRepo: AccountRepository,
    private val leaderboardRepo: LeaderboardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private val eventChannel = Channel<QuizEvent>(Channel.UNLIMITED)
    private val events: Flow<QuizEvent> = eventChannel.receiveAsFlow()

    private val effectChannel = Channel<QuizSideEffect>(Channel.UNLIMITED)
    val sideEffects: Flow<QuizSideEffect> = effectChannel.receiveAsFlow()

    private var timerJob: Job? = null

    init {

        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is QuizEvent.SubmitAnswer -> submitAnswer(event.index, event.answer)
                    is QuizEvent.TimeUp -> finishQuiz()
                    is QuizEvent.StartQuiz -> startQuiz()
                    is QuizEvent.NavigateToLeaderboard -> navigateToLeaderboard()
                    is QuizEvent.SubmitResult -> submitResult()
                    is QuizEvent.ExitQuiz            ->
                        effectChannel.send(QuizSideEffect.ShowExitConfirmation)
                    is QuizEvent.ConfirmExit         -> {
                        timerJob?.cancel()
                        effectChannel.send(QuizSideEffect.NavigateBack)
                    }
                    is QuizEvent.RequestImage -> { handleSendImage(event.imageId)}
                }
            }
        }

        sendEvent(QuizEvent.StartQuiz)
    }

    fun sendEvent(event: QuizEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun startQuiz() = viewModelScope.launch {
        if (_state.value.questions.isEmpty()) {
            if (_state.value.questions.isEmpty()) {
                repo.generateQuiz()

                val firstBatch: List<com.example.catapult.db.QuizQuestionEntity> =
                    repo.getQuestions()
                        .filter { it.isNotEmpty() }
                        .first()

                _state.update {
                    it.copy(
                        questions = firstBatch,
                        currentIndex = 0,
                        elapsedTime = 0L,
                        score = 0f,
                        finished = false
                    )
                }
                startTimer()
            }
        }
    }

    private fun submitAnswer(idx: Int, answer: String) = viewModelScope.launch {
        val questions = _state.value.questions
        val q = _state.value.questions[idx]
        val wasCorrect = (answer == q.correctAnswer)

        repo.answerQuestion(q, answer)

        val updatedList = questions.toMutableList().apply {
            this[idx] = q.copy(isCorrect = wasCorrect)
        }
        _state.update { it.copy(questions = updatedList) }

        if (idx + 1 >= _state.value.questions.size) {
            sendEvent(QuizEvent.TimeUp)
        } else {
            _state.update { it.copy(currentIndex = idx + 1) }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.elapsedTime < MAX_TIME_SECONDS) {
                delay(1_000)
                _state.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
            sendEvent(QuizEvent.TimeUp)
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()

        val s = _state.value
        val correctCount = s.questions.count { it.isCorrect == true }
        val remainingTime = maxOf(0, MAX_TIME_SECONDS - s.elapsedTime)
        val finalScore = (correctCount * 2.5f * (1 + (remainingTime + 120f) / 300f))
            .coerceAtMost(100f)

        _state.update { it.copy(
            finished = true,
            score    = finalScore
        )}

        viewModelScope.launch {
            val localNickname: String = withContext(Dispatchers.IO) {
                val accountEntity = accountRepo.getAccount().first()
                (accountEntity?.nickname ?: "")
            }

            val now = System.currentTimeMillis()
            repo.insertLocalResult(score = finalScore, createdAt = now, localNickname)
            effectChannel.send(QuizSideEffect.ShowScoreDialog(finalScore))
        }
    }

    private fun submitResult()  {
        viewModelScope.launch {
            val localNickname: String = withContext(Dispatchers.IO) {
                val accountEntity = accountRepo.getAccount().first()
                (accountEntity?.nickname ?: "")
            }
            if (localNickname.isNotBlank()) {
                withContext(Dispatchers.IO) {
                    leaderboardRepo.submitScore(localNickname, state.value.score)
                }
            }
        }

    }

    private fun navigateToLeaderboard() {
        viewModelScope.launch {
            effectChannel.send(QuizSideEffect.NavigateToLeaderboard)
        }
    }

    fun getImageEntity(imageId: String) =
        repo.getImageById(imageId)

    suspend fun handleSendImage(imageId: String) {
        val image: Flow<ImageEntity?> = getImageEntity(imageId)
        val imageData = image.first()?.toImageData()
        effectChannel.send(QuizSideEffect.SendImage(imageData))
    }

    companion object {
        private const val MAX_TIME_SECONDS = 300L
    }
}
