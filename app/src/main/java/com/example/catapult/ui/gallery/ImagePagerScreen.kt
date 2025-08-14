// app/src/main/java/com/example/catapult/ui/gallery/ImagePagerScreen.kt
package com.example.catapult.ui.gallery

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.catapult.R
import com.example.catapult.data.model.ImageData
import com.example.catapult.viewmodel.gallery.GalleryViewModel
import com.google.accompanist.pager.*
import androidx.hilt.navigation.compose.hiltViewModel
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagePagerScreen(
    breedId: String,
    initialImageId: String,
    onBack: () -> Unit,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val images by viewModel.images.collectAsState(initial = emptyList())
    val ctx = LocalContext.current

    val pagerState = rememberPagerState()

    LaunchedEffect(images, initialImageId) {
        val idx = images.indexOfFirst { it.id == initialImageId }
        if (idx >= 0) {
            pagerState.scrollToPage(idx)
        }
    }

    Scaffold(topBar = {
        Surface(
            shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TopAppBar(
                title = { Text("Photos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val img = images[page]
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(img.url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                activeColor = MaterialTheme.colorScheme.surface,
                inactiveColor = MaterialTheme.colorScheme.surfaceVariant

            )
        }
    }
}
