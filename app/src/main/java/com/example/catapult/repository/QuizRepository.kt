package com.example.catapult.repository

import com.example.catapult.db.QuizDao
import com.example.catapult.db.QuizQuestionEntity
import com.example.catapult.db.BreedDao
import com.example.catapult.db.ImageEntity
import com.example.catapult.db.BreedEntity
import com.example.catapult.db.LeaderboardDao
import com.example.catapult.db.LeaderboardEntity
import com.example.catapult.db.QuizType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val breedRepo: BreedRepository,
    private val quizDao: QuizDao,
    private val breedDao: BreedDao,
    private val leaderboardDao: LeaderboardDao
) {
    private val allTemperaments = listOf(
        "Active","Adaptable","Affectionate","Agile","Alert","Calm",
        "Clever","Curious","Demanding","Dependent","Devoted","Easy Going",
        "Energetic","Expressive","Friendly","Fun-loving","Gentle",
        "Highly interactive","Independent","Intelligent","Interactive",
        "Lively","Loving","Loyal","Mischievous","Patient","Peaceful",
        "Playful","Quiet","Relaxed","Sensible","Sensitive","Social",
        "Sweet","Talkative","Trainable","Warm"
    )

    suspend fun generateQuiz() = withContext(Dispatchers.IO) {
        quizDao.clearAll()

        var allBreeds = breedDao.getAllBreedIds()

        allBreeds = allBreeds.filter { x -> !(x.equals("mala") || x.equals("ebur")) }

        val chosenBreeds = allBreeds.shuffled().take(20)

        val questions = chosenBreeds.mapIndexed { idx, breedId ->
            breedRepo.getBreedWithImages(breedId)
            val imageIds = breedDao.getImageIdsForBreed(breedId)
            var imageId = "0"
            if (imageIds.isNotEmpty()) {
                imageId = imageIds.shuffled().first()
            }

            val type = if (idx % 2 == 0) QuizType.GUESS_BREED
            else QuizType.FIND_INTRUDER

            when (type) {
                QuizType.GUESS_BREED -> {
                    val correct = breedDao.getBreedName(breedId)
                    val wrongs  = (allBreeds - breedId)
                        .shuffled()
                        .take(3)
                        .map { breedDao.getBreedName(it) }
                    QuizQuestionEntity(
                        questionIndex = idx,
                        type = type,
                        breedId = breedId,
                        imageId = imageId,
                        correctAnswer = correct,
                        options = (listOf(correct) + wrongs).shuffled()
                    )
                }

                QuizType.FIND_INTRUDER -> {
                    val breedTemps = breedDao.getTemperament(breedId)
                        .split(",")
                        .map  { it.trim() }
                        .filter { it.isNotEmpty() }
                        .toSet()

                    val correctSet = breedTemps.shuffled().take(3)

                    val impostorOptions = allTemperaments.filter { it !in breedTemps }
                    val impostor = impostorOptions.shuffled().first()

                    val opts = (correctSet + impostor).toList().shuffled()

                    QuizQuestionEntity(
                        questionIndex = idx,
                        type = type,
                        breedId = breedId,
                        imageId = imageId,
                        correctAnswer = impostor,
                        options = opts
                    )
                }
            }
        }

        quizDao.insertAll(questions)
    }

    fun getImageById(imageId: String) = breedDao.getImageById(imageId).flowOn(Dispatchers.IO)

    fun getQuestions() = quizDao.getAllQuestions().flowOn(Dispatchers.IO)

    suspend fun answerQuestion(q: QuizQuestionEntity, answer: String) {
        val isCorrect = q.correctAnswer == answer
        if (!q.answered) {
            withContext(Dispatchers.IO) {
                quizDao.updateQuestion(q.copy(answered = true, isCorrect = isCorrect))
            }
        }
    }

    suspend fun insertLocalResult(score: Float, createdAt: Long, nickname: String) {
        withContext(Dispatchers.IO) {
            val entity = LeaderboardEntity(score = score, createdAt = createdAt, nickname = nickname)
            leaderboardDao.insert(entity)
        }
    }

    fun getAllLocalResults(nickname: String) = leaderboardDao.getAllResultsForUser(nickname).flowOn(Dispatchers.IO)
}
