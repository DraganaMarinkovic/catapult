package com.example.catapult.repository

import com.example.catapult.network.CatApi
import com.example.catapult.db.BreedDao
import com.example.catapult.db.BreedEntity
import com.example.catapult.db.ImageEntity
import com.example.catapult.db.BreedWithImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedRepository @Inject constructor(
    private val api: CatApi,
    private val dao: BreedDao
) {

    fun getBreeds(): Flow<List<BreedWithImages>> = flow {
        val currentlyInDb: List<String> = withContext(Dispatchers.IO) {
            dao.getAllBreedIds()
        }

        if (currentlyInDb.isEmpty()) {
            val remoteList = withContext(Dispatchers.IO) { api.listBreeds() }
            val now = System.currentTimeMillis()

            val breedEntities = remoteList.map { m ->
                BreedEntity(
                    id = m.id,
                    name = m.name,
                    description = m.description,
                    temperament = m.temperament,
                    origin = m.origin,
                    lifeSpan = m.lifeSpan,
                    weightMetric = m.weight?.metric,
                    weightImperial = m.weight?.imperial,
                    wikipediaUrl = m.wikipediaUrl,
                    altNames = m.altNames,
                    rare = m.rare,
                    intelligence = m.intelligence,
                    sheddingLevel = m.shedding_level,
                    affectionLevel = m.affection_level,
                    energyLevel = m.energy_level,
                    dogFriendly = m.dog_friendly,
                    lastUpdated = now
                )
            }

            withContext(Dispatchers.IO) {
                dao.upsertAll(breedEntities)
            }

            val imageEntities = remoteList.flatMap { m ->
                m.image?.let { i ->
                    listOf(
                        ImageEntity(
                            id = i.id,
                            breedId = m.id,
                            url = i.url,
                            width = i.width,
                            height = i.height
                        )
                    )
                } ?: emptyList()
            }

            withContext(Dispatchers.IO) {
                dao.upsertImages(imageEntities)
            }
        }

        emitAll(dao.getAllBreedsWithImages())
    }
        .flowOn(Dispatchers.IO)


    fun getBreedWithImages(breedId: String): Flow<BreedWithImages?> = flow {
        val cached: BreedWithImages? = withContext(Dispatchers.IO) {
            dao.getBreedWithImagesOnce(breedId)
        }

        cached?.images?.count()?.let {
            if (it <= 1) {
                try {

                    val imagesList = withContext(Dispatchers.IO) {
                        api.searchImagesByBreed(breedId) // returns List<ImageData>
                    }

                    val imageEntities = imagesList.map { apiImg ->
                        ImageEntity(
                            id = apiImg.id,
                            breedId = breedId,
                            url = apiImg.url,
                            width = apiImg.width,
                            height = apiImg.height
                        )
                    }

                    withContext(Dispatchers.IO) {
                        dao.upsertImages(imageEntities)
                    }
                } catch (ignored: Exception) {
                }
            }
        }

        emitAll(dao.getBreedWithImages(breedId))
    }
        .flowOn(Dispatchers.IO)

    fun getImagesForBreed(breedId: String): Flow<List<ImageEntity>> =
         dao.getImagesForBreed(breedId).flowOn(Dispatchers.IO)


    fun getImageById(imageId: String): Flow<ImageEntity?> =
        dao.getImageById(imageId).flowOn(Dispatchers.IO)
}
