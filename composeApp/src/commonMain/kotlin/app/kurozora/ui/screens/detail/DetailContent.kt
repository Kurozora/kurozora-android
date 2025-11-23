package app.kurozora.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.components.SectionHeader
import app.kurozora.ui.components.cards.LibraryStatusButton
import app.kurozora.ui.components.cards.RatingsAndReviewsCard
import app.kurozora.ui.components.cards.parseColor
import app.kurozora.ui.screens.explore.ItemType
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.enums.KKLibrary

@Composable
fun DetailContent(
    detail: DetailData,
    onStatusSelected: (KKLibrary.Status) -> Unit = {},
    onMarkAsWatchedClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onRemindClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
) {
    Column {
        when (detail.itemType) {
            ItemType.Show, ItemType.Literature, ItemType.Game, ItemType.Episode -> {
                // 🎬 SHOW veya LITERATURE veya GAME için bannerlı yapı
                Box {
                    // Banner
                    KamelImage(
                        resource = {
                            asyncPainterResource(
                                detail.bannerImageUrl ?: detail.coverImageUrl
                            )
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Alt bilgi bölümü
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Poster
                        KamelImage(
                            resource = { asyncPainterResource(detail.coverImageUrl) },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(95.dp)
                                .height(130.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = detail.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Spacer(modifier = Modifier.height(10.dp))
                            // Currently Airing + Rank
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                detail.status?.let {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = parseColor(it.color),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = it.name,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Rank #${detail.mediaStat?.rankGlobal.toString()}",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            // Watching + ikonlar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (detail.itemType == ItemType.Episode) {
                                    // MARK AS WATCHED Button
                                    Button(
                                        onClick = { onMarkAsWatchedClick() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(50.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            "MARK AS WATCHED",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    LibraryStatusButton(detail.library?.status, onStatusSelected)
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(onClick = onRemindClick) {
                                    val isReminded = detail.library?.isReminded == true

                                    Icon(
                                        imageVector = if (isReminded) {
                                            Icons.Default.NotificationsActive
                                        } else {
                                            Icons.Default.NotificationsNone
                                        },
                                        contentDescription = if (isReminded) "Remove Reminder" else "Set Reminder",
                                        tint = if (isReminded) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color(0xFFB0B4C3)
                                        }
                                    )
                                }


                                IconButton(onClick = onFavoriteClick) {
                                    Icon(
                                        imageVector = if (detail.library?.isFavorited == true)
                                            Icons.Default.Favorite
                                        else
                                            Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (detail.library?.isFavorited == true)
                                            Color.Red
                                        else
                                            Color(0xFFB0B4C3)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ItemType.Song -> {
                // 🎵 Song için büyük kare görsel + title + subtitle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Büyük kare görsel
                    KamelImage(
                        resource = { asyncPainterResource(detail.coverImageUrl) },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp)) // kare ama hafif köşeli
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Title
                    Text(
                        text = detail.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    // Subtitle
                    detail.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            else -> {
                // 🎯 Diğer itemType durumları (circle) + subtitle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        KamelImage(
                            resource = { asyncPainterResource(detail.coverImageUrl) },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(100.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = detail.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    // Subtitle ekledik
                    detail.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        // Stats - yatay kaydırılabilir
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(detail.stats.toList()) { (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(label.toString(), style = MaterialTheme.typography.labelSmall)
                    Text(value.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // Synopsis
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(detail.synopsisTitle ?: "Synopsis", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(detail.synopsis, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        detail.mediaStat?.let { RatingsAndReviewsCard(it) }

        Spacer(Modifier.height(16.dp))
        val columnCount = when (detail.windowWidth) {
            WindowWidthSizeClass.COMPACT -> 2 // telefonlar
            WindowWidthSizeClass.MEDIUM -> 3  // tabletler
            WindowWidthSizeClass.EXPANDED -> 4 // büyük ekranlar (laptop/masaüstü)
            else -> 2
        }

        SectionHeader(title = "Information", showSeeAll = false)
        // Info Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp) // gerekirse scroll'u sınırlamak için
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(detail.infos) { info ->
                InfoCardItem(info)
            }
        }
    }
}

@Composable
fun InfoCardItem(info: InfoCard) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
            .heightIn(min = 120.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = getInfoIcon(info.title),
                contentDescription = info.title,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = info.title,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = info.value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (info.subtitle.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = info.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
fun getInfoIcon(title: String) = when (title) {
    "Type" -> Icons.Default.Category
    "Source" -> Icons.Default.MenuBook
    "Genres" -> Icons.Default.Style
    "Themes" -> Icons.Default.Palette
    "Episodes" -> Icons.Default.List
    "Duration" -> Icons.Default.AccessTime
    "Broadcast" -> Icons.Default.Tv
    "Aired" -> Icons.Default.CalendarToday
    "TV Rating" -> Icons.Default.Star
    "Country of Origin" -> Icons.Default.Flag
    "Languages" -> Icons.Default.Language
    else -> Icons.Default.Info
}
