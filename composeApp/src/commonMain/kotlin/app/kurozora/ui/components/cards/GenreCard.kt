package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.models.genre.Genre
import org.jetbrains.compose.resources.decodeToImageBitmap

fun parseColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return Color(colorLong or 0xFF000000)
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GenreCard(
    genre: Genre,
    detailed: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (!detailed) {
        DefaultGenreCard(
            genre = genre,
            onClick = onClick,
            modifier = modifier
        )
        return
    }

    // ================= DETAILED UI =================

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            parseColor(genre.attributes.backgroundColor2),
            parseColor(genre.attributes.backgroundColor1)
        )
    )

    var patternPlaceholder by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(Unit) {
        patternPlaceholder =
            Res.readBytes("files/static/patterns/genre_pattern.svg")
    }

    Card(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .width(300.dp)
            .aspectRatio(16f / 9f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            /* -------------------- ÜST ALAN -------------------- */
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .background(backgroundGradient),
                contentAlignment = Alignment.Center
            ) {

                // Pattern arkaplan
//                patternPlaceholder?.let {
//                    Image(
//                        bitmap = it.decodeToImageBitmap(),
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop,
//                        alpha = 0.25f
//                    )
//                }

                // Genre icon (ortada)
                KamelImage(
                    resource = {
                        asyncPainterResource(
                            genre.attributes.symbol?.url.toString()
                        )
                    },
                    contentDescription = genre.attributes.name,
                    modifier = Modifier.size(110.dp),
                    contentScale = ContentScale.Fit
                )
            }

            /* -------------------- ALT ALAN -------------------- */
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.35f)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = genre.attributes.name.uppercase(),
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )

                Text(
                    text = genre.attributes.description.orEmpty(),
                    color = Color.White.copy(alpha = 0.85f),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
            }
        }
    }
}


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DefaultGenreCard(
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

    var patternPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        patternPlaceholder = Res.readBytes("files/static/patterns/genre_pattern.svg")
    }

    Card(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .width(300.dp)
            .aspectRatio(16f / 9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ÜST GÖRSEL ALAN
            Box(
                modifier = Modifier
                    .weight(1f) // üst alan
                    .fillMaxWidth()
                    .background(backgroundGradient),
                contentAlignment = Alignment.Center
            ) {
                KamelImage(
                    resource = {
                        asyncPainterResource(genre.attributes.symbol?.url.toString())
                    },
                    contentDescription = "${genre.attributes.name} symbol",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp)
                )
            }

            // ALT TEXT ALANI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(Color.Black.copy(alpha = 0.15f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = genre.attributes.name,
                    color = Color.White
                )
            }
        }
    }
}
