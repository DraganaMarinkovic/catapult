package com.example.catapult.viewmodel.quiz

import com.example.catapult.data.model.ImageData
import com.example.catapult.db.QuizQuestionEntity

data class QuizState(
    val questions: List<QuizQuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val elapsedTime: Long = 0L,
    val score: Float = 0f,
    val finished: Boolean = false
)

sealed class QuizEvent {
    object StartQuiz : QuizEvent()
    data class SubmitAnswer(val index: Int, val answer: String) : QuizEvent()
    object TimeUp : QuizEvent()
    object NavigateToLeaderboard: QuizEvent()
    object SubmitResult: QuizEvent()
    object ExitQuiz: QuizEvent()
    object ConfirmExit: QuizEvent()
    data class RequestImage(val imageId: String): QuizEvent()
}

sealed class QuizSideEffect {
    data class ShowScoreDialog(val finalScore: Float) : QuizSideEffect()
    object NavigateToLeaderboard : QuizSideEffect()
    object ShowExitConfirmation : QuizSideEffect()
    object NavigateBack : QuizSideEffect()
    data class SendImage(val image: ImageData?) : QuizSideEffect()
}