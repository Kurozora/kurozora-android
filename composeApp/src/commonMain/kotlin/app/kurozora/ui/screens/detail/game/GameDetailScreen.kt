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
import app.kurozora.ui.components.cards.CastCard
import app.kurozora.ui.components.cards.CharacterCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.components.cards.PersonCard
import app.kurozora.ui.components.cards.StudioCard
import app.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.review.Review
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    game: Game,
    windowWidth: WindowWidthSizeClass,
    isLoggedIn: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToPeopleList: (String) -> Unit,
    onNavigateToCastList: (String) -> Unit,
    onNavigateToStaffList: (String) -> Unit,
    onNavigateToCharacterList: (String) -> Unit,
    onNavigateToStudioList: (String) -> Unit,
    onNavigateToMoreByStudioList: (String) -> Unit,
    onNavigateToRelatedShowList: (String) -> Unit,
    onNavigateToRelatedLiteratureList: (String) -> Unit,
    onNavigateToRelatedGameList: (String) -> Unit,
    viewModel: GameDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchGameDetails(game.id)
    }
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game.attributes.title) },
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
            val detailLiterature = state.game ?: game
            item {
                detailLiterature.toDetailData(windowWidth, state.reviews)?.let {
                    DetailContent(
                        it,
                        onStatusSelected = { newStatus ->
                            viewModel.updateLibraryStatus(
                                state.game!!.id,
                                newStatus,
                                ItemType.Game,
                                SectionType.MainShow
                            )
                        },
                        onFavoriteClick = {
                            viewModel.updateFavoriteStatus(game.id)
                        },
                        onRemindClick = {
                            viewModel.updateReminderStatus(game.id)
                        },
                        onRateSubmit = { rating, review ->
                            viewModel.postReview(it.id,rating, review)
                        }
                    )
                }
            }
            // People
            if (state.peopleIds.isNotEmpty()) {
                item { SectionHeader(title = "People", onSeeAllClick = { onNavigateToPeopleList(game.id) }) }
                item {
                    ItemList(
                        items = state.peopleIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val person = state.people[id]
                            LaunchedEffect(id) { viewModel.fetchPerson(id) }

                            if (person != null) {
                                PersonCard(person, onClick = { onNavigateToItemDetail(person) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // Characters
            if (state.characterIds.isNotEmpty()) {
                item { SectionHeader(title = "Characters", onSeeAllClick = { onNavigateToCharacterList(game.id) }) }
                item {
                    ItemList(
                        items = state.characterIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val character = state.characters[id]
                            LaunchedEffect(id) { viewModel.fetchCharacter(id) }

                            if (character != null) {
                                CharacterCard(character, onClick = { onNavigateToItemDetail(character) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // Cast
            if (state.castIds.isNotEmpty()) {
                item { SectionHeader(title = "Cast", onSeeAllClick = { onNavigateToCastList(game.id) }) }
                item {
                    ItemList(
                        items = state.castIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val cast = state.cast[id]
                            LaunchedEffect(id) { viewModel.fetchCast(id) }

                            if (cast != null) {
                                CastCard(cast, onClick = {})
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // Staff
            if (state.staffIds.isNotEmpty()) {
                item { SectionHeader(title = "Staff", onSeeAllClick = { onNavigateToStaffList(game.id) }) }
                item {
                    ItemList(
                        items = state.staffIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val staff = state.staff[id]
                            LaunchedEffect(id) { viewModel.fetchPerson(id) }
                            val role = staff?.attributes?.role?.name
                            val person = staff?.relationships?.person?.data?.first()

                            if (person != null) {
                                PersonCard(person, subTitle = role, onClick = { onNavigateToItemDetail(person) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // Studios
            if (state.studioIds.isNotEmpty()) {
                item { SectionHeader(title = "Studios", onSeeAllClick = { onNavigateToStudioList(game.id) }) }
                item {
                    ItemList(
                        items = state.studioIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val studio = state.studios[id]
                            LaunchedEffect(id) { viewModel.fetchStudio(id) }

                            if (studio != null) {
                                StudioCard(studio = studio, onClick = { onNavigateToItemDetail(studio) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // More by Studio
            if (state.moreByStudioIds.isNotEmpty()) {
                item { SectionHeader(title = "More By Studio", onSeeAllClick = { onNavigateToMoreByStudioList(game.id) }) }
                item {
                    ItemList(
                        items = state.moreByStudioIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val game = state.moreByStudio[id]
                            LaunchedEffect(id) { viewModel.fetchMoreByStudioGame(id) }

                            if (game != null) {
                                GameCard(
                                    game = game, onClick = { onNavigateToItemDetail(game) },
                                    onStatusSelected = { newStatus ->
                                        viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game, SectionType.MoreByStudio)
                                    }
                                )
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }
            // Related Shows
            if (state.relatedShows.isNotEmpty()) {
                item { SectionHeader(title = "Related Shows", onSeeAllClick = { onNavigateToRelatedShowList(game.id) }) }
                item {
                    ItemList(
                        items = state.relatedShows,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { related ->
                            val show = related.show
                            related.attributes.relation.name
                            AnimeCard(
                                show,
                                onClick = { onNavigateToItemDetail(show) },
                                onStatusSelected = { newStatus ->
                                    viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show, SectionType.RelatedShows)
                                }
                            )
                        }
                    )
                }
            }
            // Related Literatures
            if (state.relatedLiteratures.isNotEmpty()) {
                item { SectionHeader(title = "Related Literatures", onSeeAllClick = { onNavigateToRelatedLiteratureList(game.id) }) }
                item {
                    ItemList(
                        items = state.relatedLiteratures,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { related ->
                            val lit = related.literature
                            val relation = related.attributes.relation.name
                            LiteratureCard(
                                lit,
                                topTitle = relation,
                                onClick = { onNavigateToItemDetail(lit) },
                                onStatusSelected = { newStatus ->
                                    viewModel.updateLibraryStatus(lit.id, newStatus, ItemType.Literature, SectionType.RelatedLiteratures)
                                }
                            )
                        }
                    )
                }
            }
            // Related Games
            if (state.relatedGames.isNotEmpty()) {
                item { SectionHeader(title = "Related Games", onSeeAllClick = { onNavigateToRelatedGameList(game.id) }) }
                item {
                    ItemList(
                        items = state.relatedGames,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { related ->
                            val game = related.game
                            related.attributes.relation.name
                            GameCard(
                                game,
                                onClick = { onNavigateToItemDetail(game) },
                                onStatusSelected = { newStatus ->
                                    viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game, SectionType.RelatedGames)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

fun Game?.toDetailData(windowWidth: WindowWidthSizeClass, reviews: List<Review>): DetailData? {
    return this?.let { game ->
        DetailData(
            itemType = ItemType.Game,
            windowWidth = windowWidth,
            id = game.id,
            title = game.attributes.title,
            synopsis = game.attributes.synopsis ?: "",
            coverImageUrl = game.attributes.poster?.url ?: "",
            bannerImageUrl = game.attributes.banner?.url,
            stats = mapOf(
                (game.attributes.stats?.ratingCount?.toString()
                    .orEmpty() + " reviews") to (game.attributes.stats?.ratingAverage?.toString()
                    ?: "N/A"),
                "Season" to (game.attributes.publicationSeason ?: "Unknown"),
                "Chart" to (game.attributes.stats?.rankGlobal?.toString() ?: "Unknown"),
                "Rated" to game.attributes.tvRating.name,
                "Studio" to (game.attributes.studio ?: "Unknown"),
                "Country" to (game.attributes.countryOfOrigin?.name ?: "Unknown"),
            ),
            infos = listOfNotNull(
                game.attributes.type.let {
                    InfoCard(
                        title = "Type",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                game.attributes.source.let {
                    InfoCard(
                        title = "Source",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                game.attributes.genres?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Genres",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                game.attributes.themes?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Themes",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                game.attributes.duration.let {
                    InfoCard(
                        title = "Duration",
                        value = it,
                        subtitle = ""
                    )
                },
                game.attributes.publicationTime?.let {
                    InfoCard(
                        title = "Published",
                        value = it,
                        subtitle = ""
                    )
                },
                game.attributes.tvRating.let {
                    InfoCard(
                        title = "TV Rating",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                game.attributes.countryOfOrigin?.let {
                    InfoCard(
                        title = "Country of Origin",
                        value = it.name,
                        subtitle = ""
                    )
                },
                game.attributes.languages.takeIf { it.isNotEmpty() }?.let { langs ->
                    InfoCard(
                        title = "Languages",
                        value = langs.joinToString(", ") { lang -> lang.name },
                        subtitle = ""
                    )
                }
            ),
            mediaStat = game.attributes.stats,
            status = game.attributes.status,
            library = game.attributes.library,
            reviews = reviews,
            givenRating = game.attributes.library?.rating?.toInt() ?: 0,
            givenReview = game.attributes.library?.review ?: ""
        )
    }
}
