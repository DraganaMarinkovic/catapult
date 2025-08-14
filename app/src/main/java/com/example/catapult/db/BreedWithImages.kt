// app/src/main/java/com/example/catapult/db/BreedWithImages.kt
package com.example.catapult.db

import androidx.room.Embedded
import androidx.room.Relation
import com.example.catapult.data.model.BreedApiModel
import com.example.catapult.data.model.ImageData
import com.example.catapult.data.model.Weight

data class BreedWithImages(
    @Embedded val breed: BreedEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "breedId"
    )
    val images: List<ImageEntity>
) {
    fun toApiModel(): BreedApiModel = with(breed) {
        BreedApiModel(
            id = id,
            name = name,
            temperament = temperament,
            description = description,
            origin = origin,
            lifeSpan = lifeSpan,
            weight = if (weightMetric != null && weightImperial != null)
                Weight(metric = weightMetric, imperial = weightImperial) else null,
            image = images.firstOrNull()?.let {
                ImageData(
                    id = it.id,
                    url = it.url,
                    width = it.width,
                    height = it.height
                )
            },
            wikipediaUrl = wikipediaUrl,
            altNames = altNames,
            rare = rare,
            intelligence = intelligence,
            shedding_level = sheddingLevel,
            affection_level = affectionLevel,
            energy_level = energyLevel,
            dog_friendly = dogFriendly
        )
    }
}
