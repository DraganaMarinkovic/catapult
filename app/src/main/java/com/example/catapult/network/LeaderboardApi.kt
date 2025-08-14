package com.example.catapult.network

import com.example.catapult.data.model.PostResultRequest
import com.example.catapult.data.model.PostResultResponse
import com.example.catapult.data.model.LeaderboardApiModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApi {

    @GET("leaderboard")
    suspend fun getLeaderboard(@Query("category") category: Int = 1): List<LeaderboardApiModel>

    @POST("leaderboard")
    suspend fun postResult(@Body request: PostResultRequest): PostResultResponse
}