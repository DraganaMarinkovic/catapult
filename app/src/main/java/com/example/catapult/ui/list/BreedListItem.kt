package com.example.catapult.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.catapult.data.model.BreedApiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreedListItem(
    breed: BreedApiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var ctx = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onClick(breed.id) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(breed.image?.url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "${breed.name} thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(16.dp))
                Column{
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    breed.altNames
                        ?.takeIf { it.isNotBlank() }
                        ?.let { alt ->
                            Text(
                                text = "($alt)",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                }

            }

            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp) ,
            ) {
                breed.temperament
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .take(3)
                    .forEach { item ->
                    FilterChip(
                        selected = false,
                        onClick  = { /* no-op */ },
                        label = { Text(item.trim()) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            labelColor = MaterialTheme.colorScheme.onSecondary,
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        border = null,
                        elevation = FilterChipDefaults.filterChipElevation(2.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = breed.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
