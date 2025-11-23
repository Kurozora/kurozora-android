package app.kurozora.ui.screens.detail

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
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.SectionHeader
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    song: Song,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetails: (Show) -> Unit,
    viewModel: SongDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchSongDetails(song.id)
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song.attributes.title) },
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
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            // Detail Card
            item {
                song.toDetailData(windowWidth)?.let {
                    DetailContent(
                        it,
                        onStatusSelected = { newStatus ->
                            //viewModel.updateLibraryStatus(state.show!!.id, newStatus, ItemType.Show, SectionType.MainShow)
                        })
                }
            }

            if (state.showIds.isNotEmpty()) {
                item { SectionHeader(title = "Anime") }
                item {
                    ItemList(
                        items = state.showIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val show = state.shows[id]
                            LaunchedEffect(id) { viewModel.fetchShow(id) }

                            if (show != null) {
                                AnimeCard(
                                    show,
                                    onClick = { onNavigateToAnimeDetails(show) },
                                    onStatusSelected = { newStatus ->
                                        //viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show, SectionType.RelatedShows)
                                    }
                                )
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            if (state.gameIds.isNotEmpty()) {
                item { SectionHeader(title = "Game") }
                item {
                    ItemList(
                        items = state.gameIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val game = state.games[id]
                            LaunchedEffect(id) { viewModel.fetchGame(id) }

                            if (game != null) {
                                GameCard(
                                    game,
                                    onClick = { },
                                    onStatusSelected = { newStatus ->
                                        //viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show, SectionType.RelatedShows)
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
}

fun Song?.toDetailData(windowWidth: WindowWidthSizeClass): DetailData? {
    return this?.let { song ->
        DetailData(
            itemType = ItemType.Song,
            windowWidth = windowWidth,
            id = song.id,
            title = song.attributes.title,
            synopsis = song.attributes.lyrics ?: "",
            synopsisTitle = "Lyrics",
            coverImageUrl = song.attributes.artwork?.url ?: "",
            stats = mapOf(),
            infos = listOf(),
            mediaStat = song.attributes.stats,
        )
    }
}
