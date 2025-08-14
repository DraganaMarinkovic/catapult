// app/src/main/java/com/example/catapult/db/BreedDao.kt
package com.example.catapult.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {

    @Transaction
    @Query("SELECT * FROM breeds ORDER BY name")
    fun getAllBreedsWithImages(): Flow<List<BreedWithImages>>

    @Transaction
    @Query("SELECT * FROM breeds WHERE id = :id")
    fun getBreedWithImagesOnce(id: String): BreedWithImages?

    @Transaction
    @Query("SELECT * FROM breeds WHERE id = :id")
    fun getBreedWithImages(id: String): Flow<BreedWithImages?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(breeds: List<BreedEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertImages(images: List<ImageEntity>): LongArray

    @Query("SELECT * FROM images WHERE breedId = :breedId")
    fun getImagesForBreed(breedId: String): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE id = :imageId")
    fun getImageById(imageId: String): Flow<ImageEntity?>

    @Query("SELECT id FROM breeds")
    fun getAllBreedIds(): List<String>

    @Query("SELECT name FROM breeds WHERE id = :breedId")
    fun getBreedName(breedId: String): String

    @Query("SELECT temperament FROM breeds WHERE id = :breedId")
    fun getTemperament(breedId: String): String

    @Query("SELECT id FROM images WHERE breedId = :breedId")
    fun getImageIdsForBreed(breedId: String): List<String>

    @Query("SELECT * FROM images WHERE id = :imageId")
    fun getImageEntityById(imageId: String): ImageEntity?
}
