package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.models.feed.message.FeedMessage

@Composable
fun FeedMessageCard(
    feedMessage: FeedMessage,
    onReplyClick: (FeedMessage) -> Unit,
    onReshareClick: (FeedMessage) -> Unit,
    onHeartClick: (FeedMessage) -> Unit,
    onMoreClick: (FeedMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val user = feedMessage.relationships.users.data.firstOrNull()?.attributes

    // JVM SimpleDateFormat yerine multiplatform datetime
    val createdAt = feedMessage.attributes.createdAt
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = "${createdAt.dayOfMonth} ${createdAt.month.name.lowercase().replaceFirstChar { it.uppercase() }}"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        if (feedMessage.attributes.isReShare) {
            Text(
                text = "${user?.username ?: "Unknown"} reposted this",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Profil fotoğrafı
            KamelImage(
                resource = { asyncPainterResource(user?.profile?.url.toString()) },
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user?.username ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "@${user?.username ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (feedMessage.attributes.isPinned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // İçerik
                var showMessage by remember { mutableStateOf(false) }

                if (feedMessage.attributes.isNSFW || feedMessage.attributes.isSpoiler) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2D2D2D), shape = MaterialTheme.shapes.small)
                            .clickable { showMessage = !showMessage }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (!showMessage) {
                                "This message is NSFW and contains spoilers - tap to view"
                            } else {
                                "Tap to hide message"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (showMessage) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = AnnotatedString(feedMessage.attributes.content),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    Text(
                        text = AnnotatedString(feedMessage.attributes.content),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Alt bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onReplyClick(feedMessage) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Reply",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = feedMessage.attributes.metrics.replyCount.toString(),
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onReshareClick(feedMessage) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Reshare",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = feedMessage.attributes.metrics.reShareCount.toString(),
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onHeartClick(feedMessage) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Heart",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = feedMessage.attributes.metrics.heartCount.toString(),
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onMoreClick(feedMessage) }
                    )
                }
            }
        }
    }
}
