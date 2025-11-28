package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.review.Review

@Composable
fun ReviewCard(
    review: Review,
    badgeIcons: List<Painter> = emptyList()
) {
    val user = review.relationships?.users?.data?.firstOrNull()

    Box(
        modifier = Modifier
            .width(300.dp)
            .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        // Tarih sağ üst köşede
        review.attributes.createdAt?.let { date ->
            Text(
                text = date.toString(),
                fontSize = 12.sp,
                color = Color(0xFF9AA4BF),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KamelImage(
                    resource = { asyncPainterResource(user?.attributes?.profile?.url ?: "") },
                    contentDescription = user?.attributes?.username,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    // Kullanıcı adı ve badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.attributes?.username ?: "Unknown",
                            fontSize = 16.sp,
                            color = Color.White
                        )

                        Spacer(Modifier.width(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            badgeIcons.forEach {
                                Image(
                                    painter = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Yıldızlar kullanıcı adının altında
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        val score = review.attributes.score.toInt()
                        val totalStars = 5

                        repeat(score) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        repeat(totalStars - score) {
                            Icon(
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            review.attributes.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

