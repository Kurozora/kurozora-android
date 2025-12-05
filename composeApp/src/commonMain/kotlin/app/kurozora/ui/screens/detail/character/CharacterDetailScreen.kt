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
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.components.cards.PersonCard
import app.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.review.Review
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    character: Character,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToAnimeList: (String) -> Unit,
    onNavigateToMangaList: (String) -> Unit,
    onNavigateToGameList: (String) -> Unit,
    onNavigateToPeopleList: (String) -> Unit,
    viewModel: CharacterDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchCharacterDetails(character.id)
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.attributes.name) },
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
                character.toDetailData(windowWidth, state.reviews)?.let {
                    DetailContent(
                        it,
                        onStatusSelected = { newStatus ->

                        },
                        onRateSubmit = { rating, review ->
                            viewModel.postReview(it.id,rating, review)
                        },
                    )
                }
            }

            if (state.showIds.isNotEmpty()) {
                item { SectionHeader(title = "Anime", onSeeAllClick = { onNavigateToAnimeList(character.id) }) }
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
                                    onClick = { onNavigateToItemDetail(show) },
                                    onStatusSelected = { newStatus ->
                                        viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show, SectionType.RelatedShows)
                                    }
                                )
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            if (state.literatureIds.isNotEmpty()) {
                item { SectionHeader(title = "Manga", onSeeAllClick = { onNavigateToMangaList(character.id) }) }
                item {
                    ItemList(
                        items = state.literatureIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val literature = state.literatures[id]
                            LaunchedEffect(id) { viewModel.fetchLiterature(id) }

                            if (literature != null) {
                                LiteratureCard(
                                    literature,
                                    onClick = { onNavigateToItemDetail(literature) },
                                    onStatusSelected = { newStatus ->
                                        viewModel.updateLibraryStatus(literature.id, newStatus, ItemType.Literature, SectionType.RelatedLiteratures)
                                    }
                                )
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            if (state.peopleIds.isNotEmpty()) {
                item { SectionHeader(title = "People", onSeeAllClick = { onNavigateToPeopleList(character.id) }) }
                item {
                    ItemList(
                        items = state.peopleIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val person = state.people[id]
                            LaunchedEffect(id) { viewModel.fetchPerson(id) }

                            if (person != null) {
                                PersonCard(person, onClick = { })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            if (state.gameIds.isNotEmpty()) {
                item { SectionHeader(title = "Game", onSeeAllClick = { onNavigateToGameList(character.id) }) }
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
                                        viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game, SectionType.RelatedGames)
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

fun Character?.toDetailData(windowWidth: WindowWidthSizeClass, reviews: List<Review>): DetailData? {
    return this?.let { character ->
        DetailData(
            itemType = ItemType.Character,
            windowWidth = windowWidth,
            id = character.id,
            title = character.attributes.name,
            synopsis = character.attributes.about ?: "",
            synopsisTitle = "About",
            coverImageUrl = character.attributes.profile?.url ?: "",
            stats = mapOf(),
            infos = listOf(
                character.attributes.debut.let {
                    InfoCard(
                        title = "Debut",
                        value = it ?: "-",
                        subtitle = ""
                    )
                },
                character.attributes.age.let {
                    InfoCard(
                        title = "Age",
                        value = it ?: "-",
                        subtitle = ""
                    )
                },
                character.attributes.debut.let {
                    InfoCard(
                        title = "Measurements",
                        value = "-",
                        subtitle = ""
                    )
                },
                character.attributes.let {
                    InfoCard(
                        title = "Characteristics",
                        value = "-",
                        subtitle = ""
                    )
                }
            ),
            mediaStat = character.attributes.stats,
            reviews = reviews,
//            givenRating = character.attributes.library?.rating?.toInt() ?: 0,
//            givenReview = character.attributes.library?.review ?: "",
        )
    }
}
