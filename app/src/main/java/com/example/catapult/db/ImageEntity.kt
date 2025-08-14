package com.example.catapult.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.catapult.data.model.ImageData

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = BreedEntity::class,
            parentColumns = ["id"],
            childColumns = ["breedId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("breedId")]
)
data class ImageEntity(
    @PrimaryKey val id: String,
    val breedId: String,
    val url: String,
    val width: Int?,
    val height: Int?
)

fun ImageEntity.toImageData() = ImageData(
    id = id,
    url = url,
    width = width,
    height = height
)
