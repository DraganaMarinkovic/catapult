package com.example.catapult.ui.gallery

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.catapult.data.model.ImageData
import com.example.catapult.navigation.Screen
import com.example.catapult.ui.components.SearchBar
import com.example.catapult.ui.list.BreedListBottomNav
import com.example.catapult.viewmodel.gallery.GalleryViewModel
import com.example.catapult.viewmodel.list.BreedListEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    breedId: String,
    onImageClick: (String) -> Unit,
    viewModel: GalleryViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val images by viewModel.images.collectAsState()

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
                        title = { Text("Gallery") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        }) {padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { img: ImageData ->
                AsyncImage(
                    model = img.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = { onImageClick(img.id) })
                )
            }
        }
    }
}
