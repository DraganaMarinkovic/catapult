package com.example.catapult.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BreedEntity::class, ImageEntity::class, AccountEntity::class,
        QuizQuestionEntity::class, LeaderboardEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CatapultDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao
    abstract fun accountDao(): AccountDao
    abstract fun quizDao(): QuizDao
    abstract fun leaderboardDao(): LeaderboardDao
}
