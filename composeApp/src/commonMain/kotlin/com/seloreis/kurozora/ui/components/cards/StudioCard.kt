package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozoraapp.composeapp.generated.resources.Res
import kurozorakit.data.models.studio.Studio
import org.jetbrains.compose.resources.decodeToImageBitmap

/**
 * StudioCard:
 * - Üstte banner (arka plan gibi)
 * - Banner'ın ortasında logo
 * - Altında stüdyo adı
 * - Altında küçük, transparan subtitle
 */
@Composable
fun StudioCard(
    studio: Studio,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var studioPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        studioPlaceholder = Res.readBytes("files/static/placeholders/studio_profile.webp")
    }
    Card(
        modifier = modifier
            .width(320.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent, // 🔹 arka plan yok
            contentColor = Color.Unspecified
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            // Banner alanı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                // Arka plan banner resmi
                KamelImage(
                    resource = { asyncPainterResource(studio.attributes.banner?.url ?: "") },
                    contentDescription = "${studio.attributes.name} banner",
                    modifier = Modifier
                        .matchParentSize()
                        .zIndex(0f), // banner en altta
                    contentScale = ContentScale.Crop,
                    onFailure = {
                        studioPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Placeholder avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    onLoading = {
                        studioPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Loading avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                )

                // Orta kısımda logo
                KamelImage(
                    resource = { asyncPainterResource(studio.attributes.logo?.url ?: "") },
                    contentDescription = "${studio.attributes.name} logo",
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(12.dp))
                        .zIndex(1f), // logo üstte görünür
                    contentScale = ContentScale.Crop
                )
            }


            Spacer(modifier = Modifier.height(10.dp))

            // Studio adı
            Text(
                text = studio.attributes.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            val raw = studio.attributes.foundedAtTimestamp.toString()
            val safeText = when {
                raw.equals("null", ignoreCase = true) -> ""    // "null" stringi geliyorsa
                raw.isBlank() -> ""                             // boşsa
                else -> raw
            }

            Text(
                text = safeText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                ),
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
