package com.seloreis.kurozora.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.SectionHeader
import com.seloreis.kurozora.ui.components.cards.AnimeCard
import com.seloreis.kurozora.ui.components.cards.GameCard
import com.seloreis.kurozora.ui.components.cards.LiteratureCard
import com.seloreis.kurozora.ui.components.cards.MediaCardViewMode
import com.seloreis.kurozora.ui.screens.detail.ItemPlaceholder
import com.seloreis.kurozora.ui.screens.explore.ItemType
import com.seloreis.kurozora.ui.screens.search.FilterBottomSheet
import kotlinx.coroutines.delay
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    viewModel: LibraryViewModel = koinViewModel(),
    onNavigateToFavoriteScreen: () -> Unit,
    onNavigateToReminderScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(state.query) }

    // 🔁 Debounce mantığı
    LaunchedEffect(text) {
        delay(1000) // kullanıcı yazmayı bıraktıktan sonra 1 saniye bekle
        if (text != state.query) {
            viewModel.search(text)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 1️⃣ Başlık
//                        Text(
//                            text = when (state.selectedTab) {
//                                LibraryTab.Animes -> "Anime Library"
//                                LibraryTab.Mangas -> "Manga Library"
//                                LibraryTab.Games -> "Game Library"
//                            },
//                            style = MaterialTheme.typography.titleMedium,
//                            maxLines = 1
//                        )
//
//                        Spacer(modifier = Modifier.height(4.dp))

                        // 2️⃣ Search Bar
                        TextField(
                            value = text,
                            onValueChange = {
                                text = it
                                //viewModel.search(it)
                            },
                            placeholder = {
                                Text(
                                    text = "Search...",
                                    //fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                //fontSize = 12.sp
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (text.isNotEmpty()) {
                                    IconButton(onClick = { text = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToFavoriteScreen() }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                        )
                    }
                    IconButton(onClick = { onNavigateToReminderScreen() }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Reminders",
                        )
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Üst sekmeler
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                LibraryTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = index == state.selectedTab.ordinal,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.name) }
                    )
                }
            }

            // Filtre chipleri
            if (!state.query.isNotBlank()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = KKLibrary.Status.all) { status ->
                        val isSelected = status == state.selectedStatus
                        val icon = when (status) {
                            KKLibrary.Status.INPROGRESS -> Icons.Default.PlayArrow
                            KKLibrary.Status.PLANNING -> Icons.Default.Schedule
                            KKLibrary.Status.COMPLETED -> Icons.Default.Done
                            KKLibrary.Status.ONHOLD -> Icons.Default.Pause
                            KKLibrary.Status.DROPPED -> Icons.Default.Clear
                            KKLibrary.Status.INTERESTED -> Icons.Default.Star
                            KKLibrary.Status.IGNORED -> Icons.Default.Block
                            else -> Icons.Default.Help
                        }
                        val displayLabel = when {
                            status == KKLibrary.Status.INPROGRESS && state.selectedTab == LibraryTab.Animes -> "Watching"
                            status == KKLibrary.Status.INPROGRESS && state.selectedTab == LibraryTab.Mangas -> "Reading"
                            status == KKLibrary.Status.INPROGRESS && state.selectedTab == LibraryTab.Games -> "Playing"
                            else -> status.stringValue
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectStatus(status) },
                            label = { Text(displayLabel) },
                            leadingIcon = {
                                Icon(imageVector = icon, contentDescription = displayLabel)
                            }
                        )
                    }
                }
            }

            // İçerik
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Text(
                        "Error: ${state.errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                        // 🔍 Search aktifse ID listesine göre detaylı liste göster
                        if (state.query.isNotBlank()) {
                            when (state.selectedTab) {

                                LibraryTab.Animes -> {
                                    if (state.showIds.isNotEmpty()) {
                                        item {
                                            ItemList(
                                                items = state.showIds,
                                                viewMode = ItemListViewMode.Grid(columnCount),
                                                itemContent = { id ->
                                                    val show = state.shows[id]

                                                    LaunchedEffect(id) {
                                                        if (show == null) viewModel.fetchShow(id)
                                                    }

                                                    if (show != null) {
                                                        AnimeCard(
                                                            show = show,
                                                            onClick = { onNavigateToItemDetail(show) },
                                                            viewMode = state.mediaCard,
                                                            onStatusSelected = { newStatus ->
                                                                viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show)
                                                            }
                                                        )
                                                    } else {
                                                        ItemPlaceholder()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                LibraryTab.Mangas -> {
                                    if (state.literatureIds.isNotEmpty()) {
                                        item {
                                            ItemList(
                                                items = state.literatureIds,
                                                viewMode = ItemListViewMode.Grid(columnCount),
                                                itemContent = { id ->
                                                    val lit = state.literatures[id]

                                                    LaunchedEffect(id) {
                                                        if (lit == null) viewModel.fetchLiterature(id)
                                                    }

                                                    if (lit != null) {
                                                        LiteratureCard(
                                                            lit,
                                                            onClick = { onNavigateToItemDetail(lit) },
                                                            viewMode = state.mediaCard,
                                                            onStatusSelected = { newStatus ->
                                                                viewModel.updateLibraryStatus(lit.id, newStatus, ItemType.Literature)
                                                            }
                                                        )
                                                    } else {
                                                        ItemPlaceholder()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                LibraryTab.Games -> {
                                    if (state.gameIds.isNotEmpty()) {
                                        item {
                                            ItemList(
                                                items = state.gameIds,
                                                viewMode = ItemListViewMode.Grid(columnCount),
                                                itemContent = { id ->
                                                    val game = state.games[id]

                                                    LaunchedEffect(id) {
                                                        if (game == null) viewModel.fetchGame(id)
                                                    }

                                                    if (game != null) {
                                                        GameCard(
                                                            game,
                                                            onClick = { onNavigateToItemDetail(game) },
                                                            onStatusSelected = { newStatus ->
                                                                viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game)
                                                            }
                                                        )
                                                    } else {
                                                        ItemPlaceholder()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // normal liste
                            item {
                            ItemList(
                                items = state.items,
                                viewMode = ItemListViewMode.Grid(columnCount),
                                itemContent = { item ->
                                    when (item) {
                                        is Show -> AnimeCard(
                                            show = item,
                                            onClick = { onNavigateToItemDetail(item) },
                                            viewMode = state.mediaCard,
                                            onStatusSelected = { newStatus ->
                                                viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Show) }
                                        )

                                        is Literature -> LiteratureCard(
                                            item,
                                            onClick = { onNavigateToItemDetail(item) },
                                            viewMode = state.mediaCard,
                                            onStatusSelected = { newStatus ->
                                                viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Literature)
                                            }
                                        )

                                        is Game -> GameCard(
                                            item,
                                            onClick = { onNavigateToItemDetail(item) },
                                            onStatusSelected = { newStatus ->
                                                viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Game)
                                            }
                                        )

                                        else -> Text(text = item.toString())
                                    }
                                }
                            )
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
                activeType = state.selectedTab.toSearchType(),
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
