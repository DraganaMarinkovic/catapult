package com.example.catapult.repository

import com.example.catapult.network.LeaderboardApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class LeaderboardEntry(
    val rank: Int,
    val nickname: String,
    val result: Float,
    val totalPlayed: Int
)

@Singleton
class LeaderboardRepository @Inject constructor(
    private val api: LeaderboardApi
) {

    suspend fun fetchLeaderboard(): List<LeaderboardEntry> = withContext(Dispatchers.IO) {
        val raw: List<com.example.catapult.data.model.LeaderboardApiModel> =
            api.getLeaderboard(category = 1)

        val counts: Map<String, Int> = raw
            .groupingBy { it.nickname }
            .eachCount()

        raw.mapIndexed { idx, qr ->
            LeaderboardEntry(
                rank = idx + 1,
                nickname = qr.nickname,
                result = qr.result,
                totalPlayed = counts[qr.nickname] ?: 1
            )
        }
    }

    suspend fun submitScore(nickname: String, result: Float): Int = withContext(Dispatchers.IO) {
        val request = com.example.catapult.data.model.PostResultRequest(
            nickname = nickname,
            result = result,
            category = 1
        )
        val response = api.postResult(request)
        response.ranking
    }
}
