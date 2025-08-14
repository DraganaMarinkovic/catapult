package com.example.catapult.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: LeaderboardEntity)

    @Query("SELECT * FROM leaderboard WHERE nickname = :nick ORDER BY createdAt ASC")
    fun getAllResultsForUser(nick: String): Flow<List<LeaderboardEntity>>

    @Query("SELECT MAX(score) FROM leaderboard WHERE nickname = :nick")
    fun getBestLocalScore(nick: String): Float?
}
