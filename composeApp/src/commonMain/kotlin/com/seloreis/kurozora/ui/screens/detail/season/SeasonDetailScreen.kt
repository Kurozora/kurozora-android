package com.seloreis.kurozora.ui.screens.detail.season

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.SectionHeader
import com.seloreis.kurozora.ui.components.cards.AnimeCard
import com.seloreis.kurozora.ui.components.cards.EpisodeCard
import com.seloreis.kurozora.ui.components.cards.SeasonCard
import com.seloreis.kurozora.ui.screens.detail.ItemPlaceholder
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDetailScreen(
    season: Season,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToEpisodeDetails: (Episode) -> Unit,
    viewModel: SeasonDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchSeasonEpisodes(season.id)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(season.attributes.title) },
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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            item {
                SeasonCard(season, onClick = {})
            }

            if (state.episodeIds.isNotEmpty()) {
                item {
                    SectionHeader(title = "Episodes", showSeeAll = false)

                    val columnCount = when (windowWidth) {
                        WindowWidthSizeClass.COMPACT -> 1   // 📱 Telefon
                        WindowWidthSizeClass.MEDIUM -> 2    // 💻 Küçük tablet
                        WindowWidthSizeClass.EXPANDED -> 3  // 🖥️ Büyük ekran
                        else -> 2
                    }
                    ItemList(
                        items = state.episodeIds,
                        viewMode = ItemListViewMode.Grid(columnCount),
                        itemContent = { id ->
                            val episode = state.episodes[id]
                            LaunchedEffect(id) { viewModel.fetchEpisode(id) }

                            if (episode != null) {
                                EpisodeCard(
                                    episode = episode,
                                    onClick = { onNavigateToEpisodeDetails(episode) },
                                    onMarkAsWatchedClick = { viewModel.markEpisodeAsWatched(episode.id) })
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