package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.season.Season
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DefaultLocale")
@Composable
fun SeasonCard(
    season: Season,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val attr = season.attributes
    val formattedDate = attr.startedAtTimestamp?.let {
        val date = Date(it * 1000)
        SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(date)
    } ?: "N/A"

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Poster
        KamelImage({ asyncPainterResource(attr.poster?.url.toString()) },
            contentDescription = attr.title,
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            alpha = DefaultAlpha
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Right content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .align(Alignment.Top),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // === ÜST BLOK ===
            Column {
                Text(
                    text = "Season ${attr.number}",
                    color = Color(0xFFB0B4C3),
                    fontSize = 13.sp
                )

                Text(
                    text = attr.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // === ALT BLOK ===
            Column {
                InfoRow(label = "Premiere", value = formattedDate)
                HorizontalDivider(
                    Modifier.padding(vertical = 4.dp),
                    1.dp,
                    Color.White.copy(alpha = 0.1f)
                )

                InfoRow(label = "Episodes", value = attr.episodeCount.toString())
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.1f)
                )

                InfoRow(label = "Score", value = String.format("%.1f", attr.ratingAverage))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFB0B4C3),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
