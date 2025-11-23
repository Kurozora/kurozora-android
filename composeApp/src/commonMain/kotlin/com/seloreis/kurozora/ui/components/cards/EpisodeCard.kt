package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.episode.Episode
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.enums.WatchStatus

@Composable
fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
    onMarkAsWatchedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val attributes = episode.attributes
    val date = attributes.startedAt
        ?.toLocalDateTime(TimeZone.currentSystemDefault())
        ?.let { "${it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }} ${it.dayOfMonth}, ${it.year}" }
        ?: ""

    Column(
        modifier = modifier
            .width(300.dp)
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        // Poster
        KamelImage(
            resource = { asyncPainterResource(attributes.banner?.url.toString()) },
            contentDescription = attributes.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Season + Episode Number
        Text(
            text = "S${attributes.seasonNumber} • E${attributes.number}",
            color = androidx.compose.ui.graphics.Color.Gray,
            fontSize = 14.sp
        )

        // Episode Title
        Text(
            text = attributes.title,
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 18.sp,
        )

        // Show Title
        Text(
            text = attributes.showTitle,
            color = androidx.compose.ui.graphics.Color(0xFFFF9F0A),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Views + Date
        Text(
            text = "${attributes.viewCount} views • $date",
            color = androidx.compose.ui.graphics.Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // MARK AS WATCHED Button
        Button(
            onClick = { onMarkAsWatchedClick() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text(
                text = if (episode.attributes.watchStatus == WatchStatus.watched) {
                    "WATCHED"
                } else {
                    "MARK AS WATCHED"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
