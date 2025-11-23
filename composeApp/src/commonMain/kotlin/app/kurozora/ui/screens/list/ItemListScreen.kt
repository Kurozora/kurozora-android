package app.kurozora.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.CastCard
import app.kurozora.ui.components.cards.CharacterCard
import app.kurozora.ui.components.cards.EpisodeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.GenreCard
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.components.cards.PersonCard
import app.kurozora.ui.components.cards.RecapCard
import app.kurozora.ui.components.cards.SeasonCard
import app.kurozora.ui.components.cards.SongCard
import app.kurozora.ui.components.cards.StudioCard
import app.kurozora.ui.components.cards.ThemeCard
import app.kurozora.ui.components.cards.UserCard
import app.kurozora.ui.screens.explore.ItemPlaceholder
import app.kurozora.ui.screens.explore.ItemType
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.explore.ExploreCategory
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.genre.Genre
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.recap.Recap
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.cast.Cast
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.staff.Staff
import kurozorakit.data.models.studio.Studio
import kurozorakit.data.models.theme.Theme
import kurozorakit.data.models.user.User
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(
    title: String,
    subtitle: String = "",
    itemType: ItemType,
    preloadedItems: Map<String, Any>? = null,
    fetcher: suspend (next: String?, limit: Int) -> Pair<List<String>, String?>,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    viewModel: ItemListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    // Eğer preloadedItems varsa, doğrudan state'i güncelle
    LaunchedEffect(preloadedItems) {
        if (preloadedItems != null) {
            viewModel.setPreloadedItems(preloadedItems)
        } else {
            fetcher.let { viewModel.loadInitial(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.error}")
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var columnCount = when (windowWidth) {
                    WindowWidthSizeClass.COMPACT -> 1   // 📱 Telefon
                    WindowWidthSizeClass.MEDIUM -> 3    // 💻 Küçük tablet
                    WindowWidthSizeClass.EXPANDED -> 4  // 🖥️ Büyük ekran
                    else -> 3
                }

                columnCount = when (itemType) {
                    ItemType.Person, ItemType.Character -> 3
                    else -> columnCount
                }

                item {
                    ItemList(
                        items = state.itemIds,
                        viewMode = ItemListViewMode.Grid(columnCount),
                        itemContent = { itemId ->
                            val item = state.items[itemId]

                            LaunchedEffect(itemId) {
                                if (item == null) viewModel.fetchItemDetail(itemId, itemType)
                            }

                            if (item == null) {
                                ItemPlaceholder()
                            } else {
                                when (itemType) {
                                    ItemType.Show -> (item as? Show)?.let {
                                        AnimeCard(it, onClick = { onNavigateToItemDetail(it) }, onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(itemId = it.id, newStatus, type = ItemType.Show)
                                        })
                                    } ?: ItemPlaceholder()

                                    ItemType.Literature -> (item as? Literature)?.let {
                                        LiteratureCard(it, onClick = { onNavigateToItemDetail(it) }, onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(itemId = it.id, newStatus, type = ItemType.Literature)
                                        })
                                    } ?: ItemPlaceholder()

                                    ItemType.Game -> (item as? Game)?.let {
                                        GameCard(it, onClick = {}, onStatusSelected = { newStatus ->
                                            viewModel.updateLibraryStatus(itemId = it.id, newStatus, type = ItemType.Game)
                                        })
                                    } ?: ItemPlaceholder()

                                    ItemType.Character -> (item as? Character)?.let {
                                        CharacterCard(it, { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.Person -> (item as? Person)?.let {
                                        PersonCard(it, onClick = { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.Genre -> (item as? Genre)?.let {
                                        GenreCard(it, onClick = {})
                                    } ?: ItemPlaceholder()

                                    ItemType.Theme -> (item as? Theme)?.let {
                                        ThemeCard(it, onClick = {})
                                    } ?: ItemPlaceholder()

                                    ItemType.Song -> (item as? Song)?.let {
                                        SongCard(it, onClick = { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.Episode -> (item as? Episode)?.let {
                                        EpisodeCard(it, onClick = { onNavigateToItemDetail(it) }, {
                                            viewModel.markEpisodeAsWatched(it.id)
                                        })
                                    } ?: ItemPlaceholder()

                                    ItemType.Recap -> (item as? Recap)?.let {
                                        RecapCard(it, onClick = {})
                                    } ?: ItemPlaceholder()

                                    ItemType.Studio -> (item as? Studio)?.let {
                                        StudioCard(it, onClick = { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.Season -> (item as? Season)?.let {
                                        SeasonCard(it, onClick = { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.Cast -> (item as? Cast)?.let {
                                        CastCard(it, onClick = { onNavigateToItemDetail(it) })
                                    } ?: ItemPlaceholder()

                                    ItemType.User -> (item as? User)?.let { user ->
                                        UserCard(user, onClick = { onNavigateToItemDetail(user) }, onFollowButtonClick = {
                                            viewModel.followUser(user.id)
                                        })
                                    } ?: ItemPlaceholder()

                                    ItemType.Staff -> (item as? Staff)?.let {
                                        val role = item.attributes.role.name
                                        val person = item.relationships.person.data.first()
                                        PersonCard(person, subTitle = role, onClick = { onNavigateToItemDetail(person) })
                                    } ?: ItemPlaceholder()
                                }
                            }
                        }
                    )
                }

                if (state.next != null) {
                    item {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMore(fetcher) },
                                enabled = !state.isLoadingMore
                            ) {
                                if (state.isLoadingMore)
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                else Text("Load More")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun extractRelationshipIds(category: ExploreCategory): List<String> {
    val rel = category.relationships ?: return emptyList()

    return when {
        rel.shows?.data != null -> rel.shows!!.data.map { it.id }
        rel.games?.data != null -> rel.games!!.data.map { it.id }
        rel.literatures?.data != null -> rel.literatures!!.data.map { it.id }
        rel.characters?.data != null -> rel.characters!!.data.map { it.id }
        rel.people?.data != null -> rel.people!!.data.map { it.id }
        rel.genres?.data != null -> rel.genres!!.data.map { it.id }
        rel.themes?.data != null -> rel.themes!!.data.map { it.id }
        rel.showSongs?.data != null -> rel.showSongs!!.data.map { it.id }
        rel.episodes?.data != null -> rel.episodes!!.data.map { it.id }
        rel.recaps?.data != null -> rel.recaps!!.data.map { it.id }
        else -> emptyList()
    }
}

fun extractRelationshipNext(category: ExploreCategory): String? {
    val rel = category.relationships ?: return null

    return when {
        rel.shows?.data != null -> rel.shows!!.next
        rel.games?.data != null -> rel.games!!.next
        rel.literatures?.data != null -> rel.literatures!!.next
        rel.characters?.data != null -> rel.characters!!.next
        rel.people?.data != null -> rel.people!!.next
        rel.genres?.data != null -> rel.genres!!.next
        rel.themes?.data != null -> rel.themes!!.next
        rel.showSongs?.data != null -> rel.showSongs!!.next
        rel.episodes?.data != null -> rel.episodes!!.next
        rel.recaps?.data != null -> rel.recaps!!.next
        else -> null
    }
}

@Composable
fun ExploreCategoryScreen(
    category: ExploreCategory,
    onNavigateBack: () -> Unit,
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
) {
    val kit: KurozoraKit = koinInject()
    val rel = category.relationships
    val type = when {
        rel?.shows != null -> ItemType.Show
        rel?.games != null -> ItemType.Game
        rel?.literatures != null -> ItemType.Literature
        rel?.characters != null -> ItemType.Character
        rel?.people != null -> ItemType.Person
        rel?.genres != null -> ItemType.Genre
        rel?.themes != null -> ItemType.Theme
        rel?.showSongs != null -> ItemType.Song
        rel?.episodes != null -> ItemType.Episode
        rel?.recaps != null -> ItemType.Recap
        else -> ItemType.Show
    }

    ItemListScreen(
        title = category.attributes.title,
        subtitle = category.attributes.description.orEmpty(),
        itemType = type,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.explore().getExplore(category.id, nextUrl, limit).onSuccess { res ->
                data = extractRelationshipIds(res.data.first())
                next = extractRelationshipNext(res.data.first())?.substringAfter("/v1/")
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}
