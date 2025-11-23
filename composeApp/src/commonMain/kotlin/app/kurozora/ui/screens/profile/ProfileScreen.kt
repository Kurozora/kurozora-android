package app.kurozora.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.SectionHeader
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.LiteratureCard
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.enums.FollowStatus
import kurozorakit.data.models.user.User
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    isCurrentUser: Boolean = false,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToAnimeLibraryList: (String) -> Unit,
    onNavigateToMangaLibraryList: (String) -> Unit,
    onNavigateToGameLibraryList: (String) -> Unit,
    onNavigateToFavoriteAnimeList: (String) -> Unit,
    onNavigateToFavoriteMangaList: (String) -> Unit,
    onNavigateToFavoriteGameList: (String) -> Unit,
    onNavigateToFollowersList: (String) -> Unit,
    onNavigateToFollowingsList: (String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    var profilePlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserDetails(user.id)
        profilePlaceholder = Res.readBytes("files/static/placeholders/user_profile.webp")
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = user.attributes.username,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isCurrentUser) {
                        IconButton(onClick = { onNavigateToSettings() }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                // === Banner + Profile Image + Follow Button ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Banner image
                    KamelImage(
                        {
                            asyncPainterResource(
                                user.attributes.banner?.url ?: "static/default_banner.png"
                            )
                        },
                        contentDescription = "${user.attributes.username} banner",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                        //.blur(4.dp), // Hafif blur efekti
                        contentScale = ContentScale.Crop,
                        alpha = DefaultAlpha
                    )
                    // Gradient overlay (alttan yukarı)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                    startY = 100f
                                )
                            )
                    )
                    // Profile image (bannerin altına taşsın)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 24.dp, y = 60.dp)
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        KamelImage(
                            resource = { asyncPainterResource(user.attributes.profile?.url ?: "") },
                            contentDescription = "${user.attributes.username} avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onFailure = {
                                profilePlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Placeholder avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            },
                            onLoading = {
                                profilePlaceholder?.decodeToImageBitmap()?.let { bitmap ->
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
                    // Başlangıçta user'daki değeri kullan
                    var followStatus by remember {
                        mutableStateOf(
                            user.attributes.followStatus ?: state.followStatus
                        )
                    }
                    // Eğer state.followStatus sonradan güncellenirse, bunu da takip et
                    LaunchedEffect(state.followStatus) {
                        state.followStatus?.let {
                            followStatus = it
                        }
                    }
                    // Follow / Unfollow button (sağ altta)
                    if (!isCurrentUser) {
                        Button(
                            onClick = { viewModel.followUser(user.id) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-16).dp, y = 60.dp)
                                .height(38.dp)
                        ) {
                            Text(
                                text = when (followStatus) {
                                    FollowStatus.followed -> "FOLLOWING"
                                    //FollowStatus.pending -> "PENDING"
                                    else -> "FOLLOW"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            // === Profile info ===
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp) // Avatar alanı kadar boşluk
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        //.background(Color(0xFF1C1C28))
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = user.attributes.username,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.attributes.biography ?: "",
                        color = Color(0xFFD3D3D3),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatItem(
                            "Achievements", user.relationships?.achievements?.data?.size
                                ?: 0, onClick = { })
                        StatItem("Following", user.attributes.followingCount, onClick = { onNavigateToFollowingsList(user.id) })
                        StatItem("Followers", user.attributes.followerCount, onClick = { onNavigateToFollowersList(user.id) })
                        StatItem("Reviews", 12, onClick = { })
                    }
                }
            }
            // Gelecekte state’den gelen içerikler buraya eklenebilir
//            items(state.recentActivities ?: emptyList()) { activity ->
//                Text(
//                    text = activity.toString(),
//                    color = Color.White,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
            // Library Shows
            if (state.showsLibrary.isNotEmpty()) {
                item { SectionHeader(title = "Anime Library", onSeeAllClick = { onNavigateToAnimeLibraryList(user.id) }) }
                item {
                    ItemList(
                        items = state.showsLibrary,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { show ->
                            AnimeCard(show, onClick = { onNavigateToItemDetail(show) }, onStatusSelected = {})
                        }
                    )
                }
            }
            // Favorite Literatures
            if (state.literaturesLibrary.isNotEmpty()) {
                item { SectionHeader(title = "Manga Library", onSeeAllClick = { onNavigateToMangaLibraryList(user.id) }) }
                item {
                    ItemList(
                        items = state.literaturesLibrary,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { lit ->
                            LiteratureCard(lit, onClick = { onNavigateToItemDetail(lit) }, onStatusSelected = {})
                        }
                    )
                }
            }
            // Favorite Games
            if (state.gamesLibrary.isNotEmpty()) {
                item { SectionHeader(title = "Game Library", onSeeAllClick = { onNavigateToGameLibraryList(user.id) }) }
                item {
                    ItemList(
                        items = state.gamesLibrary,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { game ->
                            GameCard(game, onClick = { onNavigateToItemDetail(game) }, onStatusSelected = {})
                        }
                    )
                }
            }
            // Favorite Shows
            if (state.favoriteShows.isNotEmpty()) {
                item { SectionHeader(title = "Favorite Shows", onSeeAllClick = { onNavigateToFavoriteAnimeList(user.id) }) }
                item {
                    ItemList(
                        items = state.favoriteShows,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { show ->
                            AnimeCard(show, onClick = { onNavigateToItemDetail(show) }, onStatusSelected = {})
                        }
                    )
                }
            }
            // Favorite Literatures
            if (state.favoriteLiteratures.isNotEmpty()) {
                item { SectionHeader(title = "Favorite Literatures", onSeeAllClick = { onNavigateToFavoriteMangaList(user.id) }) }
                item {
                    ItemList(
                        items = state.favoriteLiteratures,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { lit ->
                            LiteratureCard(lit, onClick = { onNavigateToItemDetail(lit) }, onStatusSelected = {})
                        }
                    )
                }
            }
            // Favorite Games
            if (state.favoriteGames.isNotEmpty()) {
                item { SectionHeader(title = "Favorite Games", onSeeAllClick = { onNavigateToFavoriteGameList(user.id) }) }
                item {
                    ItemList(
                        items = state.favoriteGames,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { game ->
                            GameCard(game, onClick = { onNavigateToItemDetail(game) }, onStatusSelected = {})
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = Color(0xFFAAAAAA),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}
