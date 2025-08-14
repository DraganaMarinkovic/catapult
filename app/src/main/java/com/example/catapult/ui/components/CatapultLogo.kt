package com.example.catapult.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import com.example.catapult.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CatapultLogo() {
    Image(
        painter = painterResource(id = R.drawable.catapult_logo),
        contentDescription = "Cute cat icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(64.dp)
            .padding(8.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(16.dp)),
    )
}