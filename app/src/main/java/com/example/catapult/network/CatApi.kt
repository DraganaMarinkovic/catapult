package com.example.catapult.network

import com.example.catapult.data.model.BreedApiModel
import com.example.catapult.data.model.ImageData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApi {
    @GET("v1/breeds")
    suspend fun listBreeds(): List<BreedApiModel>

    @GET("v1/breeds/{breed_id}")
    suspend fun getBreedById(@Path("breed_id") id: String): BreedApiModel

    @GET("v1/breeds/search")
    suspend fun searchBreeds(@Query("q") query: String): List<BreedApiModel>

    @GET("v1/images/{image_id}")
    suspend fun getImageById(@Path("image_id") id: String): ImageData

    @GET("v1/images/search")
    suspend fun searchImagesByBreed(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int  = 0
    ): List<ImageData>
}
