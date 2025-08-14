package com.example.catapult.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey val questionIndex: Int,
    val type: QuizType,
    val breedId: String,
    val imageId: String,
    val correctAnswer: String,
    val options: List<String> = emptyList<String>(),
    val answered: Boolean = false,
    val isCorrect: Boolean? = null
)