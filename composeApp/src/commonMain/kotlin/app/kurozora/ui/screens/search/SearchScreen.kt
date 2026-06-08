package app.kurozora.ui.screens.search

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
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
import app.kurozora.ui.screens.search.filters.FilterBottomSheet
import kotlinx.coroutines.delay
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.enums.GameType
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.enums.LiteratureType
import kurozorakit.data.enums.ShowType
import kurozorakit.data.enums.StudioType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.FilterValue
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.StudioFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun SearchScreen(
    windowWidth: WindowWidthSizeClass,
    isLoggedIn: Boolean,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToAirSeason: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    // SearchBar states
    var active by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf(state.query) }

    val browseTypes = remember {
        listOf(
            KKSearchType.shows,
            KKSearchType.literatures,
            KKSearchType.games,
            KKSearchType.characters,
            KKSearchType.people,
            KKSearchType.studios,
            KKSearchType.songs,
            KKSearchType.episodes,
            KKSearchType.users,
        )
    }

    // Suggestions geldiğinde active'i true yap
    LaunchedEffect(state.suggestions) {
        if (state.suggestions.isNotEmpty() && query.isNotEmpty()) {
            active = true
        }
    }

    // Debounce search
    LaunchedEffect(query) {
        if (query != state.query) {
            delay(500)
            viewModel.search(query)
        } else if (query.isEmpty()) {
            viewModel.fetchSuggestions("")
        } else {
            viewModel.fetchSuggestions(query)
        }
    }

    Scaffold(
        topBar = {
            // Her zaman normal TopAppBar göster
            TopAppBar(
                title = { Text(state.activeType?.displayName() ?: "Search") },
                navigationIcon = {
                    if (state.activeType != null) {
                        IconButton(onClick = { viewModel.clearActiveType() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    // Filter button - her zaman göster
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    // Air Season button - her zaman göster
                    IconButton(onClick = { onNavigateToAirSeason() }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Air Season")
                    }
                }
            )
        },
        bottomBar = {
            // SearchBar bottom bar'da
            BottomSearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    active = it.isNotEmpty()
                },
                onSearch = {
                    viewModel.search(query)
                    active = false
                },
                active = active,
                onActiveChange = { active = it },
                suggestions = state.suggestions,
                onSuggestionClick = { suggestion ->
                    query = suggestion
                    viewModel.searchWithSuggestion(suggestion)
                    active = false
                },
                onClear = {
                    query = ""
                    viewModel.search("")
                    active = false
                },
                selectedTypes = state.selectedTypes,
                onToggleType = { type ->
                    viewModel.toggleType(type)
                    // Toggle sonrası mevcut query ile yeniden ara
                    if (query.isNotEmpty()) {
                        viewModel.search(query)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading && query.isNotEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && query.isNotEmpty() -> {
                    ErrorState(
                        message = state.errorMessage!!,
                        onRetry = { viewModel.search(query) }
                    )
                }

                state.activeType == null -> {
                    // Browse content - no search query
                    BrowseContent(
                        browseTypes = browseTypes,
                        onTypeClick = { type ->
                            viewModel.searchByType(type, "")
                        }
                    )
                }

                else -> {
                    // Search results
                    SearchResultsContent(
                        state = state,
                        windowWidth = windowWidth,
                        onNavigateToItemDetail = onNavigateToItemDetail,
                        onLoadMore = viewModel::loadMore,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onClear: () -> Unit,
    selectedTypes: Set<KKSearchType>,
    onToggleType: (KKSearchType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { onSearch() },
                    expanded = active,
                    onExpandedChange = onActiveChange,
                    placeholder = { Text("Search anime, manga, games...") },
                    leadingIcon = {
                        if (active) {
                            IconButton(onClick = {
                                onActiveChange(false)
                                //onClear()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty() && !active) {
                            IconButton(onClick = onClear) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
            },
            expanded = active,
            onExpandedChange = onActiveChange,
            shape = RoundedCornerShape(24.dp),
        ) {
            Column {
                // 🆕 Search Type Chips - suggestions'ların üstünde
                if (selectedTypes.isNotEmpty()) {
                    SearchTypeChipsCompact(
                        selectedTypes = selectedTypes,
                        onToggle = onToggleType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Suggestions listesi
                if (suggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        suggestions.forEach { suggestion ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        suggestion,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                modifier = Modifier
                                    .clickable {
                                        onSuggestionClick(suggestion)
                                        onActiveChange(false)
                                    }
                                    .fillMaxWidth()
                            )
                        }
                    }
                } else if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrowseContent(
    browseTypes: List<KKSearchType>,
    onTypeClick: (KKSearchType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Browse",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        // 2-column grid using Row with chunked
        val chunkedTypes = browseTypes.chunked(2)
        items(chunkedTypes) { rowTypes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTypes.forEach { type ->
                    Box(modifier = Modifier.weight(1f)) {
                        BrowseCard(
                            type = type,
                            onClick = { onTypeClick(type) }
                        )
                    }
                }
                if (rowTypes.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BrowseCard(
    type: KKSearchType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                    contentDescription = type.displayName(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.5f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = type.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Help,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun <T : Any> SearchResultsGrid(
    modifier: Modifier = Modifier,
    items: List<String>,
    itemMap: Map<String, T>,
    fetchItem: (String) -> Unit,
    columnCount: Int,
    isLoadingMore: Boolean,
    hasNext: Boolean?,
    onLoadMore: () -> Unit,
    itemContent: @Composable (T) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = items,
            key = { id -> id }
        ) { id ->
            LaunchedEffect(id) { fetchItem(id) }

            val item = itemMap[id]
            if (item != null) {
                itemContent(item)
            } else {
                ItemPlaceholder()
            }
        }

        if (hasNext == true && !isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onLoadMore) {
                        Text("Load More")
                    }
                }
            }
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    state: SearchState,
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    onLoadMore: (KKSearchType) -> Unit,
    viewModel: SearchViewModel
) {
    val hasResults = state.hasResults()

    if (!hasResults && state.query.isNotEmpty() && !state.isLoading) {
        EmptySearchResult(query = state.query)
        return
    }

    val availableTypes = remember(state) {
        buildList {
            if (state.showIds.isNotEmpty()) add(KKSearchType.shows)
            if (state.literatureIds.isNotEmpty()) add(KKSearchType.literatures)
            if (state.gameIds.isNotEmpty()) add(KKSearchType.games)
            if (state.characterIds.isNotEmpty()) add(KKSearchType.characters)
            if (state.peopleIds.isNotEmpty()) add(KKSearchType.people)
            if (state.episodeIds.isNotEmpty()) add(KKSearchType.episodes)
            if (state.songIds.isNotEmpty()) add(KKSearchType.songs)
            if (state.studioIds.isNotEmpty()) add(KKSearchType.studios)
            if (state.userIds.isNotEmpty()) add(KKSearchType.users)
        }
    }

    if (availableTypes.isNotEmpty()) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val selectedType = availableTypes.getOrNull(selectedTabIndex)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp,
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            ) {
                availableTypes.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            // 🆕 Tab'a tıklandığında activeType'ı güncelle
                            //selectedType?.let { viewModel.setActiveType(it) }
                        },
                        text = {
                            Text(
                                type.displayName(),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🆕 Seçili type'a göre içerik göster - filter chips de göster
            when (selectedType) {
                KKSearchType.shows -> {
                    // Filter chips - sadece shows için
                    MediaTypeFilterChips(
                        searchType = KKSearchType.shows,
                        updateFilter = viewModel::updateFilter,
                        applyFilter = viewModel::applyFilter
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.shows)
                    SearchResultsGrid(
                        items = state.showIds,
                        itemMap = state.shows,
                        fetchItem = viewModel::fetchShow,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.showNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.shows) }
                    ) { show ->
                        AnimeCard(
                            show,
                            viewMode = state.mediaCard,
                            onClick = { onNavigateToItemDetail(show) },
                            onStatusSelected = { newStatus ->
                                viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show)
                            }
                        )
                    }
                }

                KKSearchType.literatures -> {
                    MediaTypeFilterChips(
                        searchType = KKSearchType.literatures,
                        updateFilter = viewModel::updateFilter,
                        applyFilter = viewModel::applyFilter
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.literatures)
                    SearchResultsGrid(
                        items = state.literatureIds,
                        itemMap = state.literatures,
                        fetchItem = viewModel::fetchLiterature,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.literatureNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.literatures) }
                    ) { literature ->
                        LiteratureCard(
                            literature,
                            viewMode = state.mediaCard,
                            onClick = { onNavigateToItemDetail(literature) },
                            onStatusSelected = { newStatus ->
                                viewModel.updateLibraryStatus(literature.id, newStatus, ItemType.Literature)
                            }
                        )
                    }
                }

                KKSearchType.games -> {
                    MediaTypeFilterChips(
                        searchType = KKSearchType.games,
                        updateFilter = viewModel::updateFilter,
                        applyFilter = viewModel::applyFilter
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.games)
                    SearchResultsGrid(
                        items = state.gameIds,
                        itemMap = state.games,
                        fetchItem = viewModel::fetchGame,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.gameNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.games) }
                    ) { game ->
                        GameCard(
                            game,
                            onClick = { onNavigateToItemDetail(game) },
                            onStatusSelected = { newStatus ->
                                viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game)
                            }
                        )
                    }
                }

                // 🆕 Character, People, Episode, Song, Studio, Users için filter chips yok (sadece grid)
                KKSearchType.characters -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.characters)
                    SearchResultsGrid(
                        items = state.characterIds,
                        itemMap = state.characters,
                        fetchItem = viewModel::fetchCharacter,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.characterNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.characters) }
                    ) { character ->
                        CharacterCard(
                            character,
                            onClick = { onNavigateToItemDetail(character) }
                        )
                    }
                }

                KKSearchType.people -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.people)
                    SearchResultsGrid(
                        items = state.peopleIds,
                        itemMap = state.people,
                        fetchItem = viewModel::fetchPerson,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.peopleNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.people) }
                    ) { person ->
                        PersonCard(
                            person,
                            onClick = { onNavigateToItemDetail(person) }
                        )
                    }
                }

                KKSearchType.episodes -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.episodes)
                    SearchResultsGrid(
                        items = state.episodeIds,
                        itemMap = state.episodes,
                        fetchItem = viewModel::fetchEpisode,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.episodeNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.episodes) }
                    ) { episode ->
                        EpisodeCard(
                            episode,
                            onClick = { onNavigateToItemDetail(episode) },
                            onMarkAsWatchedClick = {
                                viewModel.markEpisodeAsWatched(episode.id)
                            }
                        )
                    }
                }

                KKSearchType.songs -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.songs)
                    SearchResultsGrid(
                        items = state.songIds,
                        itemMap = state.songs,
                        fetchItem = viewModel::fetchSong,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.songNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.songs) }
                    ) { song ->
                        SongCard(
                            song,
                            onClick = { onNavigateToItemDetail(song) }
                        )
                    }
                }

                KKSearchType.studios -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.studios)
                    SearchResultsGrid(
                        items = state.studioIds,
                        itemMap = state.studios,
                        fetchItem = viewModel::fetchStudio,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.studioNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.studios) }
                    ) { studio ->
                        StudioCard(
                            studio,
                            onClick = { onNavigateToItemDetail(studio) }
                        )
                    }
                }

                KKSearchType.users -> {
                    val columnCount = getColumnCount(windowWidth, state, KKSearchType.users)
                    SearchResultsGrid(
                        items = state.userIds,
                        itemMap = state.users,
                        fetchItem = viewModel::fetchUser,
                        columnCount = columnCount,
                        isLoadingMore = state.isLoadingMore,
                        hasNext = state.userNext != null,
                        onLoadMore = { onLoadMore(KKSearchType.users) }
                    ) { user ->
                        UserCard(
                            user,
                            onClick = { onNavigateToItemDetail(user) },
                            onFollowButtonClick = {
                                viewModel.followUser(user.id)
                            }
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun getColumnCount(
    windowWidth: WindowWidthSizeClass,
    state: SearchState,
    type: KKSearchType
): Int {
    return when {
        state.mediaCard == MediaCardViewMode.Compact -> state.columnCount

        type == KKSearchType.people || type == KKSearchType.characters -> {
            when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> 3
                WindowWidthSizeClass.MEDIUM -> 4
                WindowWidthSizeClass.EXPANDED -> 6
                else -> 3
            }
        }

        else -> {
            when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> 2
                WindowWidthSizeClass.MEDIUM -> 3
                WindowWidthSizeClass.EXPANDED -> 4
                else -> 2
            }
        }
    }
}

@Composable
fun EmptySearchResult(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No results found for \"$query\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun SearchTypeChipsCompact(
    selectedTypes: Set<KKSearchType>,
    onToggle: (KKSearchType) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTypes = listOf(
        KKSearchType.shows to "Animes",
        KKSearchType.literatures to "Mangas",
        KKSearchType.games to "Games",
        KKSearchType.characters to "Characters",
        KKSearchType.people to "People",
        KKSearchType.episodes to "Episodes",
        KKSearchType.songs to "Songs",
        KKSearchType.studios to "Studios",
        KKSearchType.users to "Users"
    )

    // Seçilenleri önce göster
    val sortedTypes = remember(selectedTypes) {
        val selected = allTypes.filter { selectedTypes.contains(it.first) }
        val unselected = allTypes.filter { !selectedTypes.contains(it.first) }
        selected + unselected
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(sortedTypes) { (type, displayName) ->
            val isSelected = selectedTypes.contains(type)

            FilterChip(
                selected = isSelected,
                onClick = { onToggle(type) },
                label = {
                    Text(
                        displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.height(32.dp)
            )
        }

        // Clear all button
        if (selectedTypes.isNotEmpty()) {
            item {
                FilterChip(
                    selected = false,
                    onClick = {
                        selectedTypes.forEach { onToggle(it) }
                    },
                    label = {
                        Text(
                            "Clear",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

private fun KKSearchType.displayName(): String = when (this) {
    KKSearchType.shows -> "Animes"
    KKSearchType.literatures -> "Mangas"
    else -> this.name.replaceFirstChar { it.uppercase() }
}