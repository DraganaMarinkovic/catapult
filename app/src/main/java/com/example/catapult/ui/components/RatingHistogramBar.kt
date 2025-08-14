package com.example.catapult.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingHistogramBar(
    label: String,
    rating: Int,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth()) {
            for (i in 1..maxRating) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(8.dp)
                        .padding(end = if (i < maxRating) 4.dp else 0.dp)
                        .background(
                            color = if (i <= rating)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}
