package app.kurozora.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.SectionHeader
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.CharacterCard
import app.kurozora.ui.components.cards.EpisodeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.GenreCard
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.components.cards.MediaCardViewMode
import app.kurozora.ui.components.cards.PersonCard
import app.kurozora.ui.components.cards.RecapCard
import app.kurozora.ui.components.cards.SongCard
import app.kurozora.ui.components.cards.ThemeCard
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.explore.ExploreCategory
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.genre.Genre
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.recap.Recap
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.song.ShowSong
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    isLoggedIn: Boolean,
    viewModel: ExploreViewModel = koinViewModel(),
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToCategoryDetails: (ExploreCategory) -> Unit,
    onNavigateToSchedule: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore") },
                actions = {
                    IconButton(onClick = { onNavigateToSchedule() }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Schedule"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error ?: "Unknown error")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        if (uiState.genre != null) {
                            GenreCard(genre = uiState.genre!!)
                        }
                        if (uiState.theme != null) {
                            ThemeCard(theme = uiState.theme!!)
                        }
                    }

                    itemsIndexed(
                        uiState.categories,
                        key = { _, category -> category.id }
                    ) { _, category ->
                        val categoryId = category.id
                        val itemIdentities = uiState.categoryItemIdentities[categoryId]
                            ?: emptyList()
                        val categoryItems = uiState.categoryItems[categoryId] ?: emptyMap()

                        if (category.attributes.title != "Featured") {
                            SectionHeader(
                                title = category.attributes.title,
                                subtitle = category.attributes.description ?: "",
                                onSeeAllClick = { onNavigateToCategoryDetails(category) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ItemList(
                            items = itemIdentities,
                            viewMode = ItemListViewMode.Horizontal,
                            itemContent = { itemId ->
                                val item = categoryItems[itemId]
                                val type = determineItemType(category, itemId)

                                LaunchedEffect(itemId) {
                                    if (item == null && type != ItemType.Recap) {
                                        viewModel.fetchItemDetail(categoryId, itemId, type)
                                    }
                                }
                                val animeViewMode = when (category.attributes.title) {
                                    "Featured" -> MediaCardViewMode.Big
                                    "Upcoming Shows" -> MediaCardViewMode.Big
                                    "Long Time Favorites" -> MediaCardViewMode.Detailed
                                    else -> MediaCardViewMode.List
                                }

                                when (item) {
                                    is Show -> AnimeCard(
                                        show = item,
                                        viewMode = animeViewMode,
                                        onClick = { onNavigateToItemDetail(item) },
                                        onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Show)
                                        }
                                    )

                                    is Literature -> LiteratureCard(
                                        item,
                                        onClick = { onNavigateToItemDetail(item) },
                                        onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Literature)
                                        }
                                    )

                                    is Game -> GameCard(
                                        game = item,
                                        onClick = { onNavigateToItemDetail(item) },
                                        onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(item.id, newStatus, ItemType.Game)
                                        }
                                    )

                                    is Character -> CharacterCard(item, onClick = { onNavigateToItemDetail(item) })
                                    is Person -> PersonCard(item, onClick = { onNavigateToItemDetail(item) })
                                    is Genre -> GenreCard(item, onClick = { viewModel.changeGenre(item) })
                                    is Theme -> ThemeCard(item, onClick = { viewModel.changeTheme(item) })
                                    is Song -> SongCard(item, onClick = { onNavigateToItemDetail(item) })
                                    is ShowSong -> SongCard(item.song, { onNavigateToItemDetail(item.song) })
                                    is Episode -> EpisodeCard(
                                        item,
                                        onClick = { onNavigateToItemDetail(item) },
                                        onMarkAsWatchedClick = {
                                            viewModel.markAsWatchedEpisode(item.id, categoryId)
                                        }
                                    )

                                    is Recap -> RecapCard(item, onClick = { /* ... */ })
                                    null -> ItemPlaceholder(viewMode = MediaCardViewMode.List)
                                    else -> Text(item.toString())
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kategori ve itemId üzerinden item type belirle
 */
fun determineItemType(category: ExploreCategory, itemId: String): ItemType {
    val rel = category.relationships ?: return ItemType.Show
    return when {
        rel.shows?.data?.any { it.id == itemId } == true -> ItemType.Show
        rel.games?.data?.any { it.id == itemId } == true -> ItemType.Game
        rel.literatures?.data?.any { it.id == itemId } == true -> ItemType.Literature
        rel.characters?.data?.any { it.id == itemId } == true -> ItemType.Character
        rel.people?.data?.any { it.id == itemId } == true -> ItemType.Person
        rel.genres?.data?.any { it.id == itemId } == true -> ItemType.Genre
        rel.themes?.data?.any { it.id == itemId } == true -> ItemType.Theme
        rel.showSongs?.data?.any { it.id == itemId } == true -> ItemType.Song
        rel.episodes?.data?.any { it.id == itemId } == true -> ItemType.Episode
        rel.recaps?.data?.any { it.id == itemId } == true -> ItemType.Recap
        else -> ItemType.Show
    }
}

@Composable
fun ItemPlaceholder(viewMode: MediaCardViewMode = MediaCardViewMode.List) {
    val width = when (viewMode) {
        MediaCardViewMode.Big -> 200.dp
        MediaCardViewMode.Detailed -> 150.dp
        MediaCardViewMode.List -> 240.dp
        MediaCardViewMode.Compact -> 150.dp
    }
    val height = when (viewMode) {
        MediaCardViewMode.Big -> 300.dp
        MediaCardViewMode.Detailed -> 220.dp
        MediaCardViewMode.List -> 160.dp
        MediaCardViewMode.Compact -> 150.dp
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .padding(4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
    }
}

