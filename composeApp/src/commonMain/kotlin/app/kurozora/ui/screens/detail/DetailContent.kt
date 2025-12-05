package app.kurozora.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
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
import kurozorakit.data.enums.WatchStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    detail: DetailData,
    onStatusSelected: (KKLibrary.Status) -> Unit = {},
    onMarkAsWatchedClick: () -> Unit = {},
    onRemindClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onRateSubmit: (Int, String) -> Unit = { _, _ -> },
) {
    var showRatingModal by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(detail.givenRating) }
    var reviewText by remember { mutableStateOf(detail.givenReview) }

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
                                            text = if (detail.watchStatus == WatchStatus.watched) {
                                                "WATCHED"
                                            } else {
                                                "MARK AS WATCHED"
                                            },
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
        DetailStatsRow(detail.stats)
//        LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            contentPadding = PaddingValues(horizontal = 16.dp)
//        ) {
//            items(detail.stats.toList()) { (label, value) ->
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.padding(end = 16.dp)
//                ) {
//                    Text(label.toString(), style = MaterialTheme.typography.labelSmall)
//                    Text(value.toString(), style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }

        Spacer(Modifier.height(16.dp))
        // Synopsis
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(detail.synopsisTitle ?: "Synopsis", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(detail.synopsis, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        SectionHeader(title = "Ratings & Reviews", onSeeAllClick = {  })

        detail.mediaStat?.let {
            Column {
                RatingsAndReviewsCard(it, reviews = detail.reviews)

                // Rate Button
                Button(
                    onClick = { showRatingModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rate this ${detail.itemType.name.lowercase()}")
                }
            }
        }

        // Full Screen Modal Bottom Sheet
        if (showRatingModal) {
            ModalBottomSheet(
                onDismissRequest = { showRatingModal = false },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 400.dp, max = 800.dp)
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rate ${detail.title}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        IconButton(onClick = { showRatingModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rating Stars
                    Text(
                        text = "Your Rating",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Star Rating Component
                    StarRating(
                        rating = rating,
                        onRatingChange = { rating = it },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

//                    Text(
//                        text = when (rating) {
//                            0 -> "Tap a star to rate"
//                            1 -> "Poor"
//                            2 -> "Fair"
//                            3 -> "Good"
//                            4 -> "Very Good"
//                            5 -> "Excellent"
//                            else -> ""
//                        },
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Review Text Field
                    Text(
                        text = "Your Review (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BasicTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (reviewText.isEmpty()) {
                                    Text(
                                        text = "Share your thoughts about this ${detail.itemType.name.lowercase()}...",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            onRateSubmit(rating, reviewText)
                            showRatingModal = false
                            // Reset form
                            rating = 0
                            reviewText = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = rating > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Submit Review",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        val columnCount = when (detail.windowWidth) {
            WindowWidthSizeClass.COMPACT -> 2 // telefonlar
            WindowWidthSizeClass.MEDIUM -> 3  // tabletler
            WindowWidthSizeClass.EXPANDED -> 4 // büyük ekranlar (laptop/masaüstü)
            else -> 2
        }

        detail.infos.isNotEmpty().let {
            SectionHeader(title = "Information", showSeeAll = false)
            Spacer(Modifier.height(10.dp))
            // Info Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 1000.dp)
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


// Label -> Material Icon match
fun labelIcon(label: String) = when {
    label.contains("season", ignoreCase = true) -> Icons.Default.Event
    label.contains("chart", ignoreCase = true) -> Icons.Default.BarChart
    label.contains("rated", ignoreCase = true) -> Icons.Default.Star
    label.contains("studio", ignoreCase = true) -> Icons.Default.Home
    label.contains("country", ignoreCase = true) -> Icons.Default.Public
    label.contains("review", ignoreCase = true) -> Icons.Default.Comment
    label.contains("language", ignoreCase = true) -> Icons.Default.Translate
    label.contains("previous", ignoreCase = true) -> Icons.Default.ArrowBack
    label.contains("next", ignoreCase = true) -> Icons.Default.ArrowForward
    label.contains("anime", ignoreCase = true) -> Icons.Default.Movie
    else -> null
}

@Composable
fun DetailStatsRow(stats: Map<String, String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(stats.toList()) { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 100.dp).fillMaxWidth()
            ) {
                // Label
                Text(
                    text = label.uppercase(),
                    //fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(2.dp))

                // Icon
                labelIcon(label)?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(Modifier.height(2.dp))

                // Value
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            if (label != stats.keys.last()) {
                VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 4.dp)
            }
        }
    }
}

// Star Rating Component
@Composable
fun StarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Int = 30
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxStars) {
            IconButton(
                onClick = { onRatingChange(i) },
                modifier = Modifier.size(starSize.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rate $i star${if (i > 1) "s" else ""}",
                    tint = if (i <= rating) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}