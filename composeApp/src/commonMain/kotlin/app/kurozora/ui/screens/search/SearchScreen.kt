package app.kurozora.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.SectionHeader
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.CharacterCard
import app.kurozora.ui.components.cards.EpisodeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.components.cards.MediaCardViewMode
import app.kurozora.ui.components.cards.PersonCard
import app.kurozora.ui.components.cards.SongCard
import app.kurozora.ui.components.cards.StudioCard
import app.kurozora.ui.components.cards.UserCard
import app.kurozora.ui.screens.detail.ItemPlaceholder
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.search.filters.CharacterFilterSection
import app.kurozora.ui.screens.search.filters.EpisodeFilterSection
import app.kurozora.ui.screens.search.filters.GameFilterSection
import app.kurozora.ui.screens.search.filters.LiteratureFilterSection
import app.kurozora.ui.screens.search.filters.PersonFilterSection
import app.kurozora.ui.screens.search.filters.ShowFilterSection
import app.kurozora.ui.screens.search.filters.StudioFilterSection
import kotlinx.coroutines.delay
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.enums.GameType
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.enums.LiteratureType
import kurozorakit.data.enums.ShowType
import kurozorakit.data.enums.StudioType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.CharacterFilter
import kurozorakit.data.models.search.filters.EpisodeFilter
import kurozorakit.data.models.search.filters.FilterValue
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.PersonFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.StudioFilter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToAirSeason: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.activeType?.displayName() ?: "Search") },
                navigationIcon = {
                    if (state.activeType != null) {
                        IconButton(onClick = { viewModel.clearActiveType() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (state.activeType != null) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                    // Air Season button
                    IconButton(onClick = { onNavigateToAirSeason() }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Air Season"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            var text by remember { mutableStateOf(state.query) }
            // Debounce ile arama (örneğin 500ms sonra)
            LaunchedEffect(text) {
                // Kullanıcı yazmayı bıraktıktan sonra bekle
                delay(1000)
                // Eğer text değişmediyse viewModel'e gönder
                if (text != state.query) {
                    viewModel.search(text)
                }
            }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = {
                    Text(
                        "Search...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = MaterialTheme.colorScheme.primary,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
//                    focusedContainerColor = MaterialTheme.colorScheme.surface,
//                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
//                    cursorColor = MaterialTheme.colorScheme.primary
//                )
            )
            val hasResults = listOf(
                state.characterIds,
                state.episodeIds,
                state.gameIds,
                state.showIds,
                state.literatureIds,
                state.peopleIds,
                state.seasonIds,
                state.songIds,
                state.studioIds,
                state.userIds
            ).any { it.isNotEmpty() }

            if (state.activeType == null && hasResults) {
                SearchTypeChips(
                    selectedTypes = state.selectedTypes,
                    onToggle = viewModel::toggleType
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (state.activeType != null) {
                when (state.activeType) {
                    KKSearchType.shows -> {
                        MediaTypeFilterChips(
                            searchType = KKSearchType.shows,
                            updateFilter = viewModel::updateFilter,
                            applyFilter = viewModel::applyFilter
                        )
                    }

                    KKSearchType.games -> {
                        MediaTypeFilterChips(
                            searchType = KKSearchType.games,
                            updateFilter = viewModel::updateFilter,
                            applyFilter = viewModel::applyFilter
                        )
                    }

                    KKSearchType.literatures -> {
                        MediaTypeFilterChips(
                            searchType = KKSearchType.literatures,
                            updateFilter = viewModel::updateFilter,
                            applyFilter = viewModel::applyFilter
                        )
                    }

                    KKSearchType.studios -> {
                        MediaTypeFilterChips(
                            searchType = KKSearchType.studios,
                            updateFilter = viewModel::updateFilter,
                            applyFilter = viewModel::applyFilter
                        )
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.activeType == null && !hasResults) {
                val types = KKSearchType.entries // 🔹 Kotlin 1.9+ için, tüm enum değerlerini verir

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(types) { type ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f)
                                .clickable {
                                    viewModel.searchByType(type, viewModel.state.value.query)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // 🔹 Arka plan görseli (enum adını kullanıyoruz)
                                val imagePath = "files/browse/${type.name.lowercase()}.jpg"
                                var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

                                LaunchedEffect(imagePath) {
                                    try {
                                        val bytes = Res.readBytes(imagePath)
                                        if (bytes.isNotEmpty()) {
                                            imageBitmap = bytes.decodeToImageBitmap()
                                        }
                                    } catch (_: Exception) {
                                        imageBitmap = null
                                    }
                                }

                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap!!,
                                        contentDescription = type.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                }
                                // 🔹 Başlık metni (enum’un displayName veya name değeri)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.displayName(),
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            var columnCount = when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> 1   // 📱 Telefon
                WindowWidthSizeClass.MEDIUM -> 3    // 💻 Küçük tablet
                WindowWidthSizeClass.EXPANDED -> 4  // 🖥️ Büyük ekran
                else -> 3
            }
            // People / Characters için özel ayarlama
            if (state.activeType == KKSearchType.people || state.activeType == KKSearchType.characters) {
                columnCount = when (windowWidth) {
                    WindowWidthSizeClass.COMPACT -> 3   // Telefon → 3
                    WindowWidthSizeClass.MEDIUM -> 4    // Tablet → daha geniş olabilir
                    WindowWidthSizeClass.EXPANDED -> 6  // Desktop → daha fazla
                    else -> 3
                }
            }
            // Compact view mode seçiliyse → kullanıcı ayarı override eder
            if (state.mediaCard == MediaCardViewMode.Compact) {
                columnCount = state.columnCount
            }
            val listViewMode = if (state.activeType != null) {
                ItemListViewMode.Grid(columnCount)
            } else {
                ItemListViewMode.Horizontal
            }



            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.errorMessage ?: "Unknown error")
                    }
                }

                else -> {
                    fun SearchState.shouldShow(type: KKSearchType): Boolean {
                        return (activeType == null && type in selectedTypes) || activeType == type
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        if (state.shouldShow(KKSearchType.shows)) {
                            if (state.showIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Animes", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.shows, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.showIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val show = state.shows[id]
                                            LaunchedEffect(id) { viewModel.fetchShow(id) }

                                            if (show != null) {
                                                AnimeCard(show, viewMode = state.mediaCard, onClick = { onNavigateToItemDetail(show) }, onStatusSelected = { newStatus ->
                                                    viewModel.updateLibraryStatus(itemId = show.id, newStatus, type = ItemType.Show)
                                                })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        isLoadingMore = state.isLoadingMore,
                                        onLoadMore = { viewModel.loadMore(KKSearchType.shows) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.literatures)) {
                            if (state.literatureIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Mangas", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.literatures, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.literatureIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val literature = state.literatures[id]
                                            LaunchedEffect(id) { viewModel.fetchLiterature(id) }

                                            if (literature != null) {
                                                LiteratureCard(literature, viewMode = state.mediaCard, onClick = { onNavigateToItemDetail(literature) }, onStatusSelected = { newStatus ->
                                                    viewModel.updateLibraryStatus(itemId = literature.id, newStatus, type = ItemType.Literature)
                                                })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.literatures) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.games)) {
                            if (state.gameIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Games", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.games, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.gameIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val game = state.games[id]
                                            LaunchedEffect(id) { viewModel.fetchGame(id) }

                                            if (game != null) {
                                                GameCard(game, onClick = { onNavigateToItemDetail(game) }, onStatusSelected = { newStatus ->
                                                    viewModel.updateLibraryStatus(itemId = game.id, newStatus, type = ItemType.Game)
                                                })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.games) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.episodes)) {
                            if (state.episodeIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Episodes", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.episodes, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.episodeIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val episode = state.episodes[id]
                                            LaunchedEffect(id) { viewModel.fetchEpisode(id) }

                                            if (episode != null) {
                                                EpisodeCard(episode, onClick = { onNavigateToItemDetail(episode) }, onMarkAsWatchedClick = {
                                                    viewModel.markEpisodeAsWatched(episode.id)
                                                })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.episodes) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.characters)) {
                            if (state.characterIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Characters", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.characters, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.characterIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val character = state.characters[id]
                                            LaunchedEffect(id) { viewModel.fetchCharacter(id) }

                                            if (character != null) {
                                                CharacterCard(character, onClick = { onNavigateToItemDetail(character) })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.characters) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.people)) {
                            if (state.peopleIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "People", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.people, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.peopleIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val person = state.people[id]
                                            LaunchedEffect(id) { viewModel.fetchPerson(id) }

                                            if (person != null) {
                                                PersonCard(person, onClick = { onNavigateToItemDetail(person) })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.people) }
                                    )
                                }
                            }
                        }
//                        if (activeType == null || activeType == KKSearchType.seasons) {
//                            if (state.seasonIds.isNotEmpty()) {
//                                item { SectionHeader(title = "Seasons", onSeeAllClick = { }) }
//                                item {
//                                    ItemList(
//                                        items = state.seasonIds,
//                                        viewMode = cardViewMode,
//                                        itemContent = { id ->
//                                            val season = state.seasons[id]
//                                            LaunchedEffect(id) { viewModel.fetchSeason(id) }
//
//                                            if (season != null) {
//                                                SeasonCard(season, onClick = { onNavigateToItemDetail(season) })
//                                            } else {
//                                                ItemPlaceholder()
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
                        if (state.shouldShow(KKSearchType.songs)) {
                            if (state.songIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Songs", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.songs, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.songIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val song = state.songs[id]
                                            LaunchedEffect(id) { viewModel.fetchSong(id) }

                                            if (song != null) {
                                                SongCard(song, onClick = { onNavigateToItemDetail(song) })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.songs) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.studios)) {
                            if (state.studioIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Studios", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.studios, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.studioIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val studio = state.studios[id]
                                            LaunchedEffect(id) { viewModel.fetchStudio(id) }

                                            if (studio != null) {
                                                StudioCard(studio, onClick = { onNavigateToItemDetail(studio) })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.studios) }
                                    )
                                }
                            }
                        }

                        if (state.shouldShow(KKSearchType.users)) {
                            if (state.userIds.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "Users", showSeeAll = state.activeType == null, onSeeAllClick = {
                                        viewModel.searchByType(
                                            KKSearchType.users, viewModel.state.value.query
                                        )
                                    })
                                }
                                item {
                                    ItemList(
                                        items = state.userIds,
                                        viewMode = listViewMode,
                                        itemContent = { id ->
                                            val user = state.users[id]
                                            LaunchedEffect(id) { viewModel.fetchUser(id) }

                                            if (user != null) {
                                                UserCard(user, onClick = { onNavigateToItemDetail(user) }, onFollowButtonClick = {
                                                    viewModel.followUser(user.id)
                                                })
                                            } else {
                                                ItemPlaceholder()
                                            }
                                        },
                                        onLoadMore = { viewModel.loadMore(KKSearchType.users) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // ⚙️ Filter / Sort / View BottomSheet
    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            FilterBottomSheet(
                activeType = state.activeType,
                activeFilter = state.activeFilter,
                onFilterChange = viewModel::updateFilter,
                onApply = {
                    showFilterSheet = false
                    viewModel.applyFilter()
                },
                mediaCard = state.mediaCard,
                onCardViewModeChange = viewModel::updateCardViewMode,
                columnCount = state.columnCount,
                onColumnCountChange = viewModel::updateColumnCount,
                sortType = state.sortType,
                sortOption = state.sortOption,
                applySort = viewModel::applySort
            )
        }
    }
}

@Composable
fun MediaTypeFilterChips(
    searchType: KKSearchType,
    updateFilter: (Filterable) -> Unit,
    applyFilter: () -> Unit,
) {
    val mediaTypes = when (searchType) {
        KKSearchType.shows -> ShowType.allCases.toList()
        KKSearchType.literatures -> LiteratureType.allCases.toList()
        KKSearchType.games -> GameType.allCases.toList()
        KKSearchType.studios -> StudioType.allCases.toList()
        else -> emptyList()
    }
    var selectedType by remember { mutableStateOf<Enum<*>?>(null) }

    if (mediaTypes.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(mediaTypes) { type ->
                val isSelected = selectedType == type

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedType = if (isSelected) null else type
                        println("Selected type: $selectedType")
                        val rawValue = when (val sel = selectedType) {
                            is ShowType -> sel.rawValue.toString()
                            is LiteratureType -> sel.rawValue.toString()
                            is GameType -> sel.rawValue.toString()
                            is StudioType -> sel.rawValue.toString()
                            else -> null
                        }
                        updateFilter(
                            when (searchType) {
                                KKSearchType.shows -> ShowFilter(mediaType = FilterValue(include = rawValue))
                                KKSearchType.literatures -> LiteratureFilter(mediaType = FilterValue(include = rawValue))
                                KKSearchType.games -> GameFilter(mediaType = FilterValue(include = rawValue))
                                KKSearchType.studios -> StudioFilter(type = rawValue)
                                else -> return@FilterChip
                            }
                        )
                        applyFilter()
                    },
                    label = {
                        // Burada enum'un displayName özelliğini kullanıyoruz
                        val displayText = when (type) {
                            is ShowType -> type.displayName
                            is LiteratureType -> type.displayName
                            is GameType -> type.displayName
                            is StudioType -> type.displayName
                            else -> type.name
                        }
                        Text(displayText)
                    },
                )
            }
        }
    }
}

// --- BottomSheet Content ---
@Composable
fun FilterBottomSheet(
    activeType: KKSearchType?,
    activeFilter: Filterable?,
    onFilterChange: (Filterable) -> Unit,
    onApply: () -> Unit,
    mediaCard: MediaCardViewMode,
    onCardViewModeChange: (MediaCardViewMode) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
    sortType: KKLibrary.SortType,
    sortOption: KKLibrary.Option,
    applySort: (KKLibrary.SortType, KKLibrary.Option) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Filter", "Sort", "View")

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Button(onClick = onApply, modifier = Modifier.align(Alignment.End)) {
            Text("Apply")
        }
        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> FilterTabContent(activeType, activeFilter, onFilterChange)
            1 -> SortTabContent(
                type = activeType!!,
                selectedSortType = sortType,
                selectedOption = sortOption,
                applySort = applySort
            )

            2 -> ViewTabContent(
                activeType!!,
                mediaCard,
                onCardViewModeChange,
                columnCount,
                onColumnCountChange,
            )
        }
    }
}

// --- Tab Contents ---
@Composable
fun FilterTabContent(
    activeType: KKSearchType?,
    currentFilter: Filterable?,
    onApply: (Filterable) -> Unit,
) {
    when (activeType) {
        KKSearchType.shows -> ShowFilterSection(currentFilter as? ShowFilter, onApply)
        KKSearchType.literatures -> LiteratureFilterSection(currentFilter as? LiteratureFilter, onApply)
        KKSearchType.games -> GameFilterSection(currentFilter as? GameFilter, onApply)
        KKSearchType.characters -> CharacterFilterSection(currentFilter as? CharacterFilter, onApply)
        KKSearchType.episodes -> EpisodeFilterSection(currentFilter as? EpisodeFilter, onApply)
        KKSearchType.people -> PersonFilterSection(currentFilter as? PersonFilter, onApply)
        KKSearchType.studios -> StudioFilterSection(currentFilter as? StudioFilter, onApply)
//        KKSearchType.users -> UserFilterSection(currentFilter as UserFilter, onApply)
        else -> Text("No filter options available.")
    }
}

@Composable
fun SortTabContent(
    type: KKSearchType,
    selectedSortType: KKLibrary.SortType,
    selectedOption: KKLibrary.Option,
    applySort: (KKLibrary.SortType, KKLibrary.Option) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Sort options for $type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        KKLibrary.SortType.all.forEach { sortType ->
            Text(
                text = sortType.stringValue,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            sortType.optionValue.forEach { option ->
                val isSelected =
                    selectedSortType == sortType &&
                            selectedOption == option

                SortCheckboxRow(
                    sortType = sortType,
                    option = option,
                    selected = isSelected,
                    onClick = {
                        // Aynı şey seçilmişse "none" hâline getir
                        if (isSelected) {
                            applySort(KKLibrary.SortType.NONE, KKLibrary.Option.NONE)
                        } else {
                            applySort(sortType, option)
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun SortCheckboxRow(
    sortType: KKLibrary.SortType,
    option: KKLibrary.Option,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icon = iconForSort(sortType, option)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox tarzı ikon çerçevesi
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = option.stringValue,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                LocalContentColor.current
        )
    }
}

@Composable
fun iconForSort(sortType: KKLibrary.SortType, option: KKLibrary.Option): ImageVector {
    return when (sortType) {
        KKLibrary.SortType.ALPHABETICALLY ->
            when (option) {
                KKLibrary.Option.ASCENDING -> Icons.Default.KeyboardArrowUp
                KKLibrary.Option.DESCENDING -> Icons.Default.KeyboardArrowDown
                else -> Icons.Default.Sort
            }

        KKLibrary.SortType.POPULARITY ->
            when (option) {
                KKLibrary.Option.MOST -> Icons.Default.Whatshot   // 🔥
                KKLibrary.Option.LEAST -> Icons.Default.ThumbDown // 👎
                else -> Icons.Default.Whatshot
            }

        KKLibrary.SortType.DATE ->
            when (option) {
                KKLibrary.Option.NEWEST -> Icons.Default.CalendarMonth  // ⚡
                KKLibrary.Option.OLDEST -> Icons.Default.Event  // 🕒
                else -> Icons.Default.CalendarToday
            }

        KKLibrary.SortType.RATING ->
            when (option) {
                KKLibrary.Option.BEST -> Icons.Default.Star
                KKLibrary.Option.WORST -> Icons.Default.StarBorder
                else -> Icons.Default.StarHalf
            }

        KKLibrary.SortType.MYRATING ->
            when (option) {
                KKLibrary.Option.BEST -> Icons.Default.ThumbUp
                KKLibrary.Option.WORST -> Icons.Default.ThumbDown
                else -> Icons.Default.Person
            }

        else -> Icons.Default.Help
    }
}

@Composable
fun ViewTabContent(
    type: KKSearchType,
    cardViewMode: MediaCardViewMode,
    onCardViewModeChange: (MediaCardViewMode) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
) {
    val allowedModes = listOf(
        MediaCardViewMode.List,
        MediaCardViewMode.Compact,
        MediaCardViewMode.Detailed
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "View mode settings for $type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        // View mode chip group
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            allowedModes.forEach { mode ->
                FilterChip(
                    selected = cardViewMode == mode,
                    onClick = { onCardViewModeChange(mode) },
                    label = { Text(mode.name) }
                )
            }
        }
        // Compact seçili ise slider göster
        if (cardViewMode == MediaCardViewMode.Compact) {
            Column(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "Columns: $columnCount",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value = columnCount.toFloat(),
                    onValueChange = { onColumnCountChange(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8 // (10 - 1) - 1 = 8 step
                )
            }
        }
    }
}

@Composable
fun SearchTypeChips(
    selectedTypes: Set<KKSearchType>,
    onToggle: (KKSearchType) -> Unit,
) {
    val allTypes = listOf(
        KKSearchType.characters,
        KKSearchType.episodes,
        KKSearchType.games,
        KKSearchType.shows,         // Animes
        KKSearchType.literatures,   // Mangas
        KKSearchType.people,
        KKSearchType.songs,
        KKSearchType.studios,
        KKSearchType.users
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        allTypes.forEach { type ->
            val selected = selectedTypes.contains(type)
            FilterChip(
                selected = selected,
                onClick = { onToggle(type) },
                label = { Text(type.displayName()) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null
            )
        }
    }
}

private fun KKSearchType.displayName(): String = when (this) {
    KKSearchType.shows -> "Animes"
    KKSearchType.literatures -> "Mangas"
    else -> this.name.replaceFirstChar { it.uppercase() }
}
