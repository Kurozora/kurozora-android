package com.seloreis.kurozora.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.SectionHeader
import com.seloreis.kurozora.ui.components.cards.EpisodeCard
import com.seloreis.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.episode.Episode
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(
    episode: Episode,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    viewModel: EpisodeDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchEpisodeDetails(episode.id)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(episode.attributes.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val isExpanded = windowWidth == WindowWidthSizeClass.EXPANDED

        if (isExpanded) {
            // 🔹 Geniş ekran: solda detaylar, sağda öneriler
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Sol taraf - detay içeriği (esnek alan)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    episode.toDetailData(windowWidth)?.let { DetailContent(it, onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(episode.id) }) }
                }

                // Sağ taraf - öneriler
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .fillMaxHeight()
                ) {
                    if (state.nextEpisode != null) {
                        SectionHeader(title = "Up Next", showSeeAll = false)
                        val ep = state.nextEpisode!!
                        EpisodeCard(
                            episode = ep,
                            onClick = { onNavigateToItemDetail(ep) },
                            onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(ep.id) })
                    }

                    if (state.episodeSuggestionsIds.isNotEmpty()) {
                        SectionHeader(title = "See Also", showSeeAll = false)
                        ItemList(
                            items = state.episodeSuggestionsIds,
                            viewMode = ItemListViewMode.Grid(1),
                            itemContent = { id ->
                                val episode = state.episodeSuggestions[id]
                                LaunchedEffect(id) { viewModel.fetchSuggestedEpisode(id) }

                                if (episode != null) {
                                    EpisodeCard(
                                        episode = episode,
                                        onClick = { onNavigateToItemDetail(episode) },
                                        onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(episode.id) })
                                } else {
                                    ItemPlaceholder()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    episode.toDetailData(windowWidth)?.let {
                        DetailContent(it)
                    }
                }

                if (state.nextEpisode != null) {
                    item {
                        SectionHeader(title = "Up Next", showSeeAll = false)
                        val ep = state.nextEpisode!!
                        EpisodeCard(
                            episode = ep,
                            onClick = { onNavigateToItemDetail(ep) },
                            onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(ep.id) }
                        )
                    }
                }

                if (state.episodeSuggestionsIds.isNotEmpty()) {
                    item {
                        SectionHeader(title = "See Also", showSeeAll = false)
                    }

                    // 🔹 Grid içerikli ItemList doğrudan LazyColumn içinde render edilecek
                    item {
                        val columnCount = when (windowWidth) {
                            WindowWidthSizeClass.COMPACT -> 1
                            WindowWidthSizeClass.MEDIUM -> 2
                            else -> 1
                        }

                        ItemList(
                            items = state.episodeSuggestionsIds,
                            viewMode = ItemListViewMode.Grid(columnCount),
                            modifier = Modifier.fillMaxWidth(),
                            itemContent = { id ->
                                val episode = state.episodeSuggestions[id]
                                LaunchedEffect(id) { viewModel.fetchSuggestedEpisode(id) }

                                if (episode != null) {
                                    EpisodeCard(
                                        episode = episode,
                                        onClick = { onNavigateToItemDetail(episode) },
                                        onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(episode.id) }
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

    }
}


fun Episode?.toDetailData(windowWidth: WindowWidthSizeClass): DetailData? {
    return this?.let { ep ->
        DetailData(
            itemType = ItemType.Episode,
            windowWidth = windowWidth,
            id = ep.id,
            title = ep.attributes.title,
            synopsis = ep.attributes.synopsis ?: "",
            coverImageUrl = ep.attributes.poster?.url ?: "",
            bannerImageUrl = ep.attributes.banner?.url,
            stats = mapOf(
                (ep.attributes.stats?.ratingCount?.toString()
                    .orEmpty() + " reviews") to (ep.attributes.stats?.ratingAverage?.toString()
                    ?: "N/A"),
                "Season" to (ep.attributes.seasonNumber.toString()),
                "Chart" to (ep.attributes.stats?.rankGlobal?.toString() ?: "Unknown"),
                "Previous" to ep.attributes.previousEpisodeTitle.orEmpty(),
                "Next" to (ep.attributes.nextEpisodeTitle.orEmpty()),
                "Anime" to ep.attributes.showTitle,
            ),
            infos = listOfNotNull(
                InfoCard(
                    title = "Number",
                    value = ep.attributes.number.toString(),
                    subtitle = "#${ep.attributes.number} in the current season"
                ),
                InfoCard(
                    title = "Duration",
                    value = ep.attributes.duration,
                    subtitle = ""
                ),
                InfoCard(
                    title = "Aired",
                    value = ep.attributes.startedAt.toString(),
                    subtitle = ""
                ),
            ),
            mediaStat = ep.attributes.stats,
        )
    }
}