package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.recap.Recap

@Composable
fun RecapCard(
    recap: Recap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(300.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        parseColor(recap.attributes.backgroundColor2),
                        parseColor(recap.attributes.backgroundColor1)
                    ),
                    startY = Float.POSITIVE_INFINITY, // Bottom → Top yönü
                    endY = 0f
                )
            )
    ) {
        // Resim üstte olacak
        KamelImage(
            resource = { asyncPainterResource(recap.attributes.artwork?.url.toString()) },
            contentDescription = recap.attributes.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
