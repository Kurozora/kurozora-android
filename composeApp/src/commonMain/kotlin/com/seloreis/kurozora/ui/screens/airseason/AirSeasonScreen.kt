package com.seloreis.kurozora.ui.screens.airseason

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.cards.AnimeCard
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.enums.SeasonOfYear
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirSeasonScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AirSeasonViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.selectTab(state.selectedTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Season",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri"
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
        ) {
            // Üst sekmeler
            SeasonTabs(
                tabs = state.tabs,
                selected = state.selectedTab,
                onSelect = { viewModel.selectTab(it) }
            )

            val columnCount = when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> 1   // 📱 Telefon
                WindowWidthSizeClass.MEDIUM -> 3    // 💻 Küçük tablet
                WindowWidthSizeClass.EXPANDED -> 4  // 🖥️ Büyük ekran
                else -> 3
            }
            when {
                state.selectedTab.type == SeasonTabType.Archive -> {
                    ArchiveTabContent(
                        columnCount = columnCount,
                        onSeasonSelected = { tab ->
                            viewModel.selectTab(tab)
                        }
                    )
                }
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

                        if (state.shows.isNotEmpty()) {
                            item {
                                ItemList(
                                    items = state.shows,
                                    viewMode = ItemListViewMode.Grid(columnCount),
                                    itemContent = { show ->
                                        AnimeCard(
                                            show,
                                            onClick = { onNavigateToItemDetail(show) },
                                            onStatusSelected = { newStatus ->
                                                //viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show, SectionType.RelatedShows)
                                            }
                                        )
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

@Composable
fun SeasonTabs(
    tabs: List<SeasonTab>,
    selected: SeasonTab,
    onSelect: (SeasonTab) -> Unit
) {
    val isArchive = selected.type == SeasonTabType.Archive

    val selectedIndex = if (isArchive) {
        tabs.size
    } else {
        tabs.indexOf(selected)
    }

    TabRow(
        selectedTabIndex = selectedIndex,
    ) {
        tabs.forEachIndexed { index, tab ->
            val selectedTab = selected == tab

            Tab(
                selected = selectedTab,
                onClick = { onSelect(tab) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tab.season.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // YIL BADGE
                        Badge(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .scale(0.9f),
                            containerColor = if (selectedTab)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTab)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(text = tab.year.toString())
                        }
                    }
                }
            )
        }

        Tab(
            selected = isArchive,
            onClick = { onSelect(selected.copy(type = SeasonTabType.Archive)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Archive",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // YIL BADGE
                    Badge(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .scale(0.9f),
                        containerColor = if (isArchive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isArchive)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(text = "Archive")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArchiveTabContent(
    onSeasonSelected: (SeasonTab) -> Unit,
    columnCount: Int
) {
    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val currentYear = now.year
    val startYear = currentYear + 2
    val endYear = 1917
    val years = (startYear downTo endYear).toList()

    val gridState = rememberLazyGridState()

    val thumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    val thumbColorHovered = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)

    Box(modifier = Modifier.fillMaxSize()) {

        // GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(years.size) { index ->
                val year = years[index]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        SeasonOfYear.allCases.toList().forEach { season ->
                            FilterChip(
                                selected = true,
                                onClick = {
                                    onSeasonSelected(
                                        SeasonTab(
                                            type = SeasonTabType.Normal,
                                            season = season,
                                            year = year
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        season.name.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- Scrollbar ---
//        VerticalScrollbar(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = 4.dp)
//                .fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(gridState),
//            style = LocalScrollbarStyle.current.copy(
//                thickness = 10.dp,
//                shape = RoundedCornerShape(50),
//                hoverDurationMillis = 150,
//                unhoverColor = thumbColor,
//                hoverColor = thumbColorHovered
//            )
//        )

        // --- FAST SCROLLER ---
//        FastScroller(
//            modifier = Modifier.fillMaxHeight(),
//            gridState = gridState,
//            totalItems = years.size
//        )
    }
}

@Composable
fun FastScroller(
    modifier: Modifier,
    gridState: LazyGridState,
    totalItems: Int
) {
    val coroutine = rememberCoroutineScope()

    Box(
        modifier = modifier
            .padding(end = 8.dp)
            .width(24.dp)
            .fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(8.dp)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val maxIndex = totalItems - 1
                        val proportion = delta / 2000f
                        val targetIndex =
                            (gridState.firstVisibleItemIndex + (proportion * totalItems))
                                .toInt()
                                .coerceIn(0, maxIndex)

                        coroutine.launch {
                            gridState.scrollToItem(targetIndex)
                        }
                    }
                )
        )
    }
}
