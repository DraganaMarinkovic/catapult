package com.example.catapult.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_questions ORDER BY questionIndex")
    fun getAllQuestions(): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(questions: List<QuizQuestionEntity>)

    @Update
    fun updateQuestion(q: QuizQuestionEntity)

    @Query("DELETE FROM quiz_questions")
    fun clearAll()
}
