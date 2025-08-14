package com.example.catapult.ui.details

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.catapult.data.model.BreedApiModel
import com.example.catapult.data.model.ImageData
import com.example.catapult.ui.components.ErrorScreen
import com.example.catapult.ui.components.LoadingScreen
import com.example.catapult.ui.components.RatingHistogramBar
import com.example.catapult.viewmodel.details.BreedDetailsViewModel
import androidx.core.net.toUri
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import coil.request.CachePolicy
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailsScreen(
    breedId: String,
    viewModel: BreedDetailsViewModel = hiltViewModel(),
    onGalleryClick: ()->Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    TopAppBar(
                        title = { Text(text = state.breed?.name.orEmpty()) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when {
                state.isLoading -> LoadingScreen(Modifier.fillMaxSize())
                state.error != null -> ErrorScreen(message = state.error.toString(), modifier = Modifier.fillMaxSize())
                state.breed != null -> DetailsContent(
                    breed = state.breed!!,
                    images = state.images,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onGalleryClick = onGalleryClick,  // pass down
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsContent(
    breed: BreedApiModel,
    images: List<ImageData>,
    modifier: Modifier = Modifier,
    onGalleryClick: ()->Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()).padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (images.isEmpty()) {
            Text("No photos available.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(images) { img ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(img.url)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier     = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }

        Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
            Text("See Full Gallery")
        }

        Text(breed.name, style = MaterialTheme.typography.headlineMedium)
        Text(breed.description, style = MaterialTheme.typography.bodyLarge)

        Text("Origin: ${breed.origin.orEmpty()}", style = MaterialTheme.typography.bodyMedium)
        Text("Life span: ${breed.lifeSpan.orEmpty()}", style = MaterialTheme.typography.bodyMedium)
        Text("Weight: ${breed.weight?.metric.orEmpty()} kg", style = MaterialTheme.typography.bodyMedium)
        Text("Rare breed: ${if (breed.rare > 0) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            breed.temperament.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .take(5)
                .forEach { temp ->
                    FilterChip(
                        selected = false,
                        onClick = {  },
                        label = { Text(temp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            labelColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        modifier = Modifier.wrapContentSize(),
                        border = null,
                        elevation = FilterChipDefaults.filterChipElevation(2.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
        }

        val attrs = listOf(
            "Intelligence" to breed.intelligence,
            "Energy Level" to breed.energy_level,
            "Affection Level" to breed.affection_level,
            "Dog Friendly" to breed.dog_friendly,
            "Shedding Level" to breed.shedding_level
        )

        attrs.forEach { (label, value) ->
            RatingHistogramBar(
                label = label,
                rating = value,
                maxRating = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                breed.wikipediaUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("View on Wikipedia")
        }
    }
}
