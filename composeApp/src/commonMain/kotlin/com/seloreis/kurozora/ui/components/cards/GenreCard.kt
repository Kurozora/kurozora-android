package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.genre.Genre

fun parseColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return Color(colorLong or 0xFF000000) // alpha ekliyoruz
}


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GenreCard(
    genre: Genre,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            parseColor(genre.attributes.backgroundColor2),
            parseColor(genre.attributes.backgroundColor1)
        ),
        startY = Float.POSITIVE_INFINITY,
        endY = 0f
    )

    Card(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .width(300.dp)
            .aspectRatio(16f / 9f)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundGradient)
                .fillMaxSize()
        ) {
            // Background image placeholder (SVG yerine multiplatform PNG/JPG)
            KamelImage(
                resource = { asyncPainterResource("/static/patterns/genre_pattern.png") },
                contentDescription = "${genre.attributes.name} cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Symbol image ortada
            KamelImage(
                resource = { asyncPainterResource(genre.attributes.symbol?.url.toString()) },
                contentDescription = "${genre.attributes.name} symbol",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )

            Text(genre.attributes.name)
        }
    }
}
