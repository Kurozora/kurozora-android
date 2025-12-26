package app.kurozora.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.kurozora.ui.screens.explore.ItemPlaceholder
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show
import org.jetbrains.compose.resources.decodeToImageBitmap

enum class MediaCardViewMode {
    List, Compact, Big, Detailed, Banner
}

@Composable
fun MediaCard(
    modifier: Modifier,
    title: String?,
    topTitle: String,
    description: String?,
    tagline: String,
    posterUrl: String?,
    bannerUrl: String? = null,
    onClick: () -> Unit,
    viewMode: MediaCardViewMode = MediaCardViewMode.List,
    libraryStatus: KKLibrary.Status? = null,
    onStatusSelected: (KKLibrary.Status) -> Unit,
) {
    Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
    )
    var posterPlaceholder: ByteArray? by remember { mutableStateOf(null) }
    var bannerPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        posterPlaceholder = Res.readBytes("files/static/placeholders/anime_poster.webp")
        bannerPlaceholder = Res.readBytes("files/static/placeholders/anime_banner.webp")
    }

    when (viewMode) {
        MediaCardViewMode.List -> {
            Column(
                modifier = modifier
                    .width(300.dp)
                    .height(160.dp)
//                    .fillMaxWidth()
//                    .aspectRatio(16f / 9f)
                    .clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    KamelImage(
                        resource = { asyncPainterResource(posterUrl.toString()) },
                        //fallbackResource = { asyncPainterResource(Res.drawable.static.placeholders.anime_poster) },
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(110.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp)),
                        onFailure = {
                            posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Placeholder avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        onLoading = {
                            posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Loading avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        if (topTitle.isNotEmpty()) {
                            Text(
                                text = topTitle,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = title ?: "Unknown Title",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description ?: "No description available.",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LibraryStatusButton(
                            libraryStatus = libraryStatus,
                            onStatusSelected = onStatusSelected
                        )
                    }
                }
            }
        }

        MediaCardViewMode.Compact -> {
            posterUrl?.let {
                KamelImage(
                    resource = { asyncPainterResource(it) },
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onClick),
                    onFailure = {
                        posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Placeholder avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    onLoading = {
                        posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Loading avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                )
            }
        }

        MediaCardViewMode.Big -> {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .clickable(onClick = onClick)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    // 1️⃣ Arka plan resmi
                    posterUrl?.let {
                        KamelImage(
                            resource = { asyncPainterResource(it) },
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize(),
                            onFailure = {
                                posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Placeholder avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            },
                            onLoading = {
                                posterPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Loading avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        )
                    }
                    // 2️⃣ Alt blur + fade layer (aralıktan başlayarak yukarı doğru)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        // Alt kısmı kapsayan blur
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(80.dp) // sadece alt 80dp
                                .graphicsLayer { alpha = 1f } // blur için layer
                                .blur(16.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )
                    }
                    // 3️⃣ Üstteki içerik: Text + Buton, blur’dan etkilenmez
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title ?: "Unknown Title",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LibraryStatusButton(
                            libraryStatus = libraryStatus,
                            onStatusSelected = onStatusSelected
                        )
                    }
                }
            }
        }

        MediaCardViewMode.Detailed -> {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .clickable(onClick = onClick)
                    .padding(8.dp)
            ) {
                bannerUrl?.let {
                    KamelImage(
                        resource = { asyncPainterResource(it) },
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        onFailure = {
                            bannerPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Placeholder avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        onLoading = {
                            bannerPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Loading avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    posterUrl?.let {
                        KamelImage(
                            resource = { asyncPainterResource(it) },
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(110.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            text = title ?: "Unknown Title",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description ?: "No description available.",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LibraryStatusButton(
                            libraryStatus = libraryStatus,
                            onStatusSelected = onStatusSelected
                        )
                    }
                }

                Text(
                    text = tagline,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        MediaCardViewMode.Banner -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClick)
            ) {

                // 🔹 Banner image (NET)
                KamelImage(
                    resource = { asyncPainterResource(bannerUrl ?: posterUrl ?: "") },
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    onFailure = {
                        bannerPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                )

                // 🔹 Alt blur + gradient SADECE text arkasında
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    // Blur layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer { alpha = 0.99f } // blur fix
                            .blur(18.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = title ?: "Unknown Title",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun AnimeCard(
    show: Show,
    onClick: () -> Unit,
    viewMode: MediaCardViewMode = MediaCardViewMode.List,
    onStatusSelected: (KKLibrary.Status) -> Unit,
    topTitle: String = "",
    modifier: Modifier = Modifier,
) {
    MediaCard(
        modifier = modifier,
        title = show.attributes.title,
        description = show.attributes.synopsis,
        topTitle = topTitle,
        tagline = show.attributes.tagline ?: "",
        posterUrl = show.attributes.poster?.url,
        bannerUrl = show.attributes.banner?.url,
        onClick = onClick,
        viewMode = viewMode,
        libraryStatus = show.attributes.library?.status,
        onStatusSelected = onStatusSelected
    )
}

@Composable
fun LiteratureCard(
    lit: Literature,
    onClick: () -> Unit,
    viewMode: MediaCardViewMode = MediaCardViewMode.List,
    onStatusSelected: (KKLibrary.Status) -> Unit,
    topTitle: String = "",
    modifier: Modifier = Modifier,
) {
    MediaCard(
        modifier =  modifier,
        title = lit.attributes.title,
        description = lit.attributes.synopsis,
        topTitle = topTitle,
        tagline = lit.attributes.tagline ?: "",
        posterUrl = lit.attributes.poster?.url,
        onClick = onClick,
        viewMode = viewMode,
        libraryStatus = lit.attributes.library?.status,
        onStatusSelected = onStatusSelected
    )
}

@Composable
fun LibraryStatusButton(
    libraryStatus: KKLibrary.Status?,
    onStatusSelected: (KKLibrary.Status) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(6.dp))
    // "NONE" veya null ise "Add" yazsın
    val statusText = if (libraryStatus == null || libraryStatus == KKLibrary.Status.NONE)
        "Add"
    else
        libraryStatus.stringValue

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier.height(30.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Dropdown",
            modifier = Modifier.size(16.dp)
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(200.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Statü listesi
                    KKLibrary.Status.all.forEach { status ->
                        val isSelected = libraryStatus == status
                        val buttonColors = if (isSelected) {
                            ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = {
                                showDialog = false
                                onStatusSelected(status)
                            },
                            colors = buttonColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (status) {
                                        KKLibrary.Status.INPROGRESS -> Icons.Default.PlayArrow
                                        KKLibrary.Status.PLANNING -> Icons.Default.Schedule
                                        KKLibrary.Status.COMPLETED -> Icons.Default.Done
                                        KKLibrary.Status.ONHOLD -> Icons.Default.Pause
                                        KKLibrary.Status.DROPPED -> Icons.Default.Clear
                                        KKLibrary.Status.INTERESTED -> Icons.Default.Star
                                        KKLibrary.Status.IGNORED -> Icons.Default.Block
                                        else -> Icons.Default.Help
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = status.stringValue,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    // Eğer bir status seçiliyse (ve NONE değilse) "Remove" butonunu göster
                    if (libraryStatus != null && libraryStatus != KKLibrary.Status.NONE) {
                        Divider(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .alpha(0.3f)
                        )
                        TextButton(
                            onClick = {
                                showDialog = false
                                onStatusSelected(KKLibrary.Status.NONE)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Remove",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MediaBannerPager(
    items: List<Show>,
    modifier: Modifier = Modifier,
    autoScrollDelay: Long = 4_000,
    onClick: (Show) -> Unit,
    onStatusSelected: (Show, KKLibrary.Status) -> Unit
) {
    if (items.isEmpty()) {
        ItemPlaceholder(viewMode = MediaCardViewMode.Banner)
        return
    }

    val pagerState = rememberPagerState { items.size }

    // 🧠 Kullanıcı etkileşim state'leri
    var isHovered by remember { mutableStateOf(false) }
    var isUserDragging by remember { mutableStateOf(false) }

    // 🔁 Auto scroll (PAUSE aware)
    LaunchedEffect(
        items.size,
        isHovered,
        isUserDragging
    ) {
        if (items.size <= 1) return@LaunchedEffect

        while (true) {
            kotlinx.coroutines.delay(autoScrollDelay)

            // ⛔ pause conditions
            if (isHovered || isUserDragging) continue

            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            // 🖱 Desktop hover
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    false
                },
                onExit = {
                    isHovered = false
                    false
                }
            )
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                // 👆 User swipe detection
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            isUserDragging =
                                event.changes.any { it.pressed }
                        }
                    }
                }
        ) { page ->
            val show = items[page]

            MediaCard(
                modifier = Modifier.fillMaxWidth(),
                title = show.attributes.title,
                description = show.attributes.synopsis,
                topTitle = "Featured",
                tagline = show.attributes.tagline ?: "",
                posterUrl = show.attributes.poster?.url,
                bannerUrl = show.attributes.banner?.url,
                viewMode = MediaCardViewMode.Banner,
                libraryStatus = show.attributes.library?.status,
                onClick = { onClick(show) },
                onStatusSelected = { onStatusSelected(show, it) }
            )
        }
    }

//    // 🔘 Indicator dots
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 8.dp),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        repeat(items.size) { index ->
//            Box(
//                modifier = Modifier
//                    .padding(4.dp)
//                    .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
//                    .clip(MaterialTheme.shapes.small)
//                    .background(
//                        if (pagerState.currentPage == index)
//                            MaterialTheme.colorScheme.primary
//                        else
//                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
//                    )
//            )
//        }
//    }
}

