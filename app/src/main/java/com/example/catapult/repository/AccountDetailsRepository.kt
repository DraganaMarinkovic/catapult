package com.example.catapult.repository

import com.example.catapult.db.AccountDao
import com.example.catapult.db.LeaderboardDao
import com.example.catapult.network.LeaderboardApi
import com.example.catapult.data.model.LeaderboardApiModel
import com.example.catapult.db.QuizDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountDetailsRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val leaderboardDao: LeaderboardDao,
    private val leaderboardApi: LeaderboardApi
) {
    fun getAccount(): Flow<com.example.catapult.db.AccountEntity?> =
        accountDao.getAccount()

    fun getAllLocalQuizResults(nickname: String): Flow<List<com.example.catapult.db.LeaderboardEntity>> =
        leaderboardDao.getAllResultsForUser(nickname)

    suspend fun getBestLocalScore(nickname: String): Float? =
        withContext(Dispatchers.IO) {
            leaderboardDao.getBestLocalScore(nickname)
        }

    suspend fun getGlobalLeaderboard(): List<LeaderboardApiModel> =
        withContext(Dispatchers.IO) {
            leaderboardApi.getLeaderboard()
        }
}
