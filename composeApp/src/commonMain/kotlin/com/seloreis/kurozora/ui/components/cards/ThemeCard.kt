package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozoraapp.composeapp.generated.resources.Res
import kurozorakit.data.models.theme.Theme
import org.jetbrains.compose.resources.painterResource

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ThemeCard(
    theme: Theme,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // Multiplatform’da android.graphics.Color yerine direkt Color kullanıyoruz
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            parseColor(theme.attributes.backgroundColor2 ?: "#000000"),
            parseColor(theme.attributes.backgroundColor1 ?: "#FFFFFF")
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
            // Saydam cover image (pattern)
//            Image(
//                painter = painterResource("drawable/static/patterns/genre_pattern.png"),
//                contentDescription = "${theme.attributes.name} cover",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )


            // Symbol image ortada
            KamelImage(
                resource = { asyncPainterResource(theme.attributes.symbol?.url.toString()) },
                contentDescription = "${theme.attributes.name} symbol",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            )

            Text(theme.attributes.name)
        }

    }
}
