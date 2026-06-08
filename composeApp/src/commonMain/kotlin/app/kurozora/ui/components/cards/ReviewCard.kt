package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.ui.screens.explore.ItemPlaceholder
import app.kurozora.ui.screens.explore.ItemType
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.review.Review
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.studio.Studio
import kurozorakit.data.models.user.User
import kurozorakit.shared.Result
import org.koin.compose.koinInject
import kotlin.collections.forEach

@Composable
fun ReviewCard(
    review: Review,
    hasEntity: Boolean = false,
    onNavigateToItemDetail: (Any) -> Unit = {},
    onStatusSelected: ((KKLibrary.Status) -> Unit) = {},
    modifier: Modifier = Modifier,
    kit: KurozoraKit = koinInject()
) {
    var relatedItem by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var itemType by remember { mutableStateOf<ItemType?>(null) }

    // İlişkili item'ı yükle
    LaunchedEffect(review.id) {
        val relationships = review.relationships ?: return@LaunchedEffect

        try {
            val (item, type) = when {
                relationships.shows != null -> {
                    val showId = relationships.shows!!.data.firstOrNull()?.id
                    if (showId != null) {
                        val show = (kit.show().getShow(showId) as? Result.Success)?.data?.data?.firstOrNull()
                        show to ItemType.Show
                    } else null to null
                }
                relationships.games != null -> {
                    val gameId = relationships.games!!.data.firstOrNull()?.id
                    if (gameId != null) {
                        val game = (kit.game().getGame(gameId) as? Result.Success)?.data?.data?.firstOrNull()
                        game to ItemType.Game
                    } else null to null
                }
                relationships.literatures != null -> {
                    val literatureId =  relationships.literatures!!.data.firstOrNull()?.id
                    if (literatureId != null) {
                        val literature = (kit.literature().getLiterature(literatureId) as? Result.Success)?.data?.data?.firstOrNull()
                        literature to ItemType.Literature
                    } else null to null
                }
                relationships.episodes != null -> {
                    val episodeId = relationships.episodes!!.data.firstOrNull()?.id
                    if (episodeId != null) {
                        val episode = (kit.episode().getEpisode(episodeId) as? Result.Success)?.data?.data?.firstOrNull()
                        episode to ItemType.Episode
                    } else null to null
                }
                relationships.songs != null -> {
                    val songId = relationships.songs!!.data.firstOrNull()?.id
                    if (songId != null) {
                        val song = (kit.song().getSong(songId) as? Result.Success)?.data?.data?.firstOrNull()
                        song to ItemType.Song
                    } else null to null
                }
                relationships.characters != null -> {
                    val characterId = relationships.characters!!.data.firstOrNull()?.id
                    if (characterId != null) {
                        val character = (kit.character().getCharacter(characterId) as? Result.Success)?.data?.data?.firstOrNull()
                        character to ItemType.Character
                    } else null to null
                }
                relationships.people != null -> {
                    val personId = relationships.people!!.data.firstOrNull()?.id
                    if (personId != null) {
                        val person = (kit.people().getPerson(personId) as? Result.Success)?.data?.data?.firstOrNull()
                        person to ItemType.Person
                    } else null to null
                }
                relationships.studios != null -> {
                    val studioId = relationships.studios!!.data.firstOrNull()?.id
                    if (studioId != null) {
                        val studio = (kit.studio().getStudio(studioId) as? Result.Success)?.data?.data?.firstOrNull()
                        studio to ItemType.Studio
                    } else null to null
                }
                relationships.users != null -> {
                    val userId = relationships.users!!.data.firstOrNull()?.id
                    if (userId != null) {
                        val user = (kit.auth().getUserProfile(userId) as? Result.Success)?.data?.data?.firstOrNull()
                        user to ItemType.User
                    } else null to null
                }
                else -> null to null
            }

            relatedItem = item
            itemType = type
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }


    when {
        isLoading -> {
            // Loading placeholder
            Box(
                modifier = modifier.fillMaxWidth(),
                content = { ItemPlaceholder() }
            )
        }
        relatedItem != null && itemType != null -> {
            // İlişkili item tipine göre uygun kartı göster
            when (itemType) {
                ItemType.Show -> (relatedItem as? Show)?.let { show ->
                    AnimeCard(
                        show = show,
                        onClick = { onNavigateToItemDetail(show) },
                        onStatusSelected = onStatusSelected,
                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Game -> (relatedItem as? Game)?.let { game ->
                    GameCard(
                        game = game,
                        onClick = { onNavigateToItemDetail(game) },
                        onStatusSelected = onStatusSelected,
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Literature -> (relatedItem as? Literature)?.let { literature ->
                    LiteratureCard(
                        lit = literature,
                        onClick = { onNavigateToItemDetail(literature) },
                        onStatusSelected = onStatusSelected,
                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Character -> (relatedItem as? Character)?.let { character ->
                    CharacterCard(
                        character = character,
                        onClick = { onNavigateToItemDetail(character) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Person -> (relatedItem as? Person)?.let { person ->
                    PersonCard(
                        person = person,
                        onClick = { onNavigateToItemDetail(person) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Episode -> (relatedItem as? Episode)?.let { episode ->
                    EpisodeCard(
                        episode = episode,
                        onClick = { onNavigateToItemDetail(episode) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth(),
                        onMarkAsWatchedClick = { /* Handle mark as watched click */ }
                    )
                }

                ItemType.Song -> (relatedItem as? Song)?.let { song ->
                    SongCard(
                        song = song,
                        onClick = { onNavigateToItemDetail(song) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.Studio -> (relatedItem as? Studio)?.let { studio ->
                    StudioCard(
                        studio = studio,
                        onClick = { onNavigateToItemDetail(studio) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth()
                    )
                }

                ItemType.User -> (relatedItem as? User)?.let { user ->
                    UserCard(
                        user = user,
                        onClick = { onNavigateToItemDetail(user) },
//                        topTitle = "⭐ ${review.attributes.score}/5",
                        modifier = modifier.fillMaxWidth(),
                        onFollowButtonClick = { /* Handle follow button click */ }
                    )
                }

                else -> {
                    // Fallback: review içeriğini göster
                    ReviewContentOnly(review = review, modifier = modifier)
                }
            }
        }
        else -> {
            // Fallback: sadece review içeriğini göster
            ReviewContentOnly(review = review, modifier = modifier)
        }
    }
}

@Composable
fun ReviewContentOnly(
    review: Review,
    badgeIcons: List<Painter> = emptyList(),
    modifier: Modifier = Modifier
) {
    val user = review.relationships?.users?.data?.firstOrNull()

    Box(
        modifier = modifier
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