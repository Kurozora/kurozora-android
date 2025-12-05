package app.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import app.kurozora.core.util.formatRelativeTime
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.models.feed.message.FeedMessage

@Composable
fun FeedMessageCard(
    feedMessage: FeedMessage,
    isCurrentUser: Boolean,
    onClick: () -> Unit,
    onReplyClick: (FeedMessage) -> Unit,
    onReshareClick: (FeedMessage) -> Unit,
    onHeartClick: (FeedMessage) -> Unit,
    onEditClick: (FeedMessage) -> Unit,
    onDeleteClick: (FeedMessage) -> Unit,
    onReportClick: (FeedMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdown by remember { mutableStateOf(false) }
    var dropdownOffset by remember { mutableStateOf(DpOffset.Zero) }
    var moreButtonSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    val user = feedMessage.relationships.users.data.firstOrNull()?.attributes
    val createdAtInstant = feedMessage.attributes.createdAt
    val formattedDate = formatRelativeTime(createdAtInstant)

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
            KamelImage(
                resource = { asyncPainterResource(user?.profile?.url.toString()) },
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                onFailure = {
                    // Fallback içerik
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedMessage.relationships.users.data.firstOrNull()?.attributes?.username?.first()
                                ?.uppercaseChar().toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
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
                        modifier = Modifier.fillMaxWidth().clickable { onClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val metrics = feedMessage.attributes.metrics
                val iconTint = Color.White.copy(alpha = 0.7f)
                val textTint = Color.White.copy(alpha = 0.6f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reply Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onReplyClick(feedMessage) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Reply",
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = metrics.replyCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = textTint
                            )
                        }

                        // Reshare Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onReshareClick(feedMessage) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Reshare",
                                tint = if (feedMessage.attributes.isReShared) MaterialTheme.colorScheme.primary else iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = metrics.reShareCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = textTint
                            )
                        }

                        // Heart Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onHeartClick(feedMessage) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Heart",
                                tint = if (feedMessage.attributes.isHearted == true) MaterialTheme.colorScheme.error else iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = metrics.heartCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = textTint
                            )
                        }
                    }

                    // More Button with Dropdown
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = iconTint,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    showDropdown = true
                                }
                                .onGloballyPositioned { coordinates ->
                                    moreButtonSize = coordinates.size.toSize()
                                }
                                .padding(4.dp)
                        )

                        // Dropdown Menu
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            offset = DpOffset(
                                x = with(density) { -moreButtonSize.width.toDp() },
                                y = 0.dp
                            ),
                            modifier = Modifier
                                .width(160.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            // Reply Option
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Reply,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text("Reply")
                                    }
                                },
                                onClick = {
                                    showDropdown = false
                                    onReplyClick(feedMessage)
                                }
                            )

                            // Copy Link Option
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Link,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text("Copy Link")
                                    }
                                },
                                onClick = {
                                    showDropdown = false
                                    // Implement copy link functionality
                                }
                            )

                            Divider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )

                            // Owner-only options
                            if (isCurrentUser) {
                                // Edit Option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text("Edit")
                                        }
                                    },
                                    onClick = {
                                        showDropdown = false
                                        onEditClick(feedMessage)
                                    }
                                )

                                // Delete Option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                "Delete",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    onClick = {
                                        showDropdown = false
                                        onDeleteClick(feedMessage)
                                    }
                                )
                            } else {
                                // Report Option for non-owners
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Report,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text("Report")
                                        }
                                    },
                                    onClick = {
                                        showDropdown = false
                                        onReportClick(feedMessage)
                                    }
                                )

                                // Mute User Option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.NotificationsOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text("Mute User")
                                        }
                                    },
                                    onClick = {
                                        showDropdown = false
                                        // Implement mute functionality
                                    }
                                )

                                // Block User Option
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Block,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                "Block User",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    onClick = {
                                        showDropdown = false
                                        // Implement block functionality
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}