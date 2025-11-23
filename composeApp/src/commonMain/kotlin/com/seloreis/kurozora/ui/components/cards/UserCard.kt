package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import kurozorakit.data.enums.FollowStatus
import kurozorakit.data.models.user.User

@Composable
fun UserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    followStatus: FollowStatus = user.attributes.followStatus,
    onFollowButtonClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .width(300.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Profil resmi
        KamelImage({ asyncPainterResource(user.attributes.profile?.url ?: "") },
            contentDescription = "${user.attributes.username} avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            alpha = DefaultAlpha
        )

        // Kullanıcı adı
        Text(
            text = user.attributes.username,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f)
        )

        // Follow / Unfollow butonu
        val isFollowing = followStatus == FollowStatus.followed
        val buttonText = if (isFollowing) "Unfollow" else "Follow"

        Button(
            onClick = { onFollowButtonClick(user.id) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
