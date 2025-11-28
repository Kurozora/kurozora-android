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
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.review.Review
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiteratureDetailScreen(
    literature: Literature,
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
    viewModel: LiteratureDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchLiteratureDetails(literature.id)
    }
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(literature.attributes.title) },
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
            val detailLiterature = state.literature ?: literature
            item {
                detailLiterature.toDetailData(windowWidth, state.reviews)?.let {
                    DetailContent(
                        it,
                        onStatusSelected = { newStatus ->
                            viewModel.updateLibraryStatus(
                                state.literature!!.id,
                                newStatus,
                                ItemType.Literature,
                                SectionType.MainShow
                            )
                        },
                        onFavoriteClick = {
                            viewModel.updateFavoriteStatus(literature.id)
                        },
                        onRemindClick = {
                            viewModel.updateReminderStatus(literature.id)
                        },
                    )
                }
            }
            // People
            if (state.peopleIds.isNotEmpty()) {
                item { SectionHeader(title = "People", onSeeAllClick = { onNavigateToPeopleList(literature.id) }) }
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
            // Cast
            if (state.castIds.isNotEmpty()) {
                item { SectionHeader(title = "Cast", onSeeAllClick = { onNavigateToCastList(literature.id) }) }
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
                item { SectionHeader(title = "Staff", onSeeAllClick = { onNavigateToStaffList(literature.id) }) }
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
            // Characters
            if (state.characterIds.isNotEmpty()) {
                item { SectionHeader(title = "Characters", onSeeAllClick = { onNavigateToCharacterList(literature.id) }) }
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
            // Studios
            if (state.studioIds.isNotEmpty()) {
                item { SectionHeader(title = "Studios", onSeeAllClick = { onNavigateToStudioList(literature.id) }) }
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
                item { SectionHeader(title = "More By Studio", onSeeAllClick = { onNavigateToMoreByStudioList(literature.id) }) }
                item {
                    ItemList(
                        items = state.moreByStudioIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val lit = state.moreByStudio[id]
                            LaunchedEffect(id) { viewModel.fetchMoreByStudioLiterature(id) }

                            if (lit != null) {
                                LiteratureCard(
                                    lit,
                                    onClick = { onNavigateToItemDetail(lit) },
                                    onStatusSelected = { newStatus ->
                                        viewModel.updateLibraryStatus(lit.id, newStatus, ItemType.Literature, SectionType.MoreByStudio)
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
                item { SectionHeader(title = "Related Shows", onSeeAllClick = { onNavigateToRelatedShowList(literature.id) }) }
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
                item { SectionHeader(title = "Related Literatures", onSeeAllClick = { onNavigateToRelatedLiteratureList(literature.id) }) }
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
                item { SectionHeader(title = "Related Games", onSeeAllClick = { onNavigateToRelatedGameList(literature.id) }) }
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

fun Literature?.toDetailData(windowWidth: WindowWidthSizeClass, reviews: List<Review>): DetailData? {
    return this?.let { lit ->
        DetailData(
            itemType = ItemType.Literature,
            windowWidth = windowWidth,
            id = lit.id,
            title = lit.attributes.title,
            synopsis = lit.attributes.synopsis ?: "",
            coverImageUrl = lit.attributes.poster?.url ?: "",
            bannerImageUrl = lit.attributes.banner?.url,
            stats = mapOf(
                (lit.attributes.stats?.ratingCount?.toString()
                    .orEmpty() + " reviews") to (lit.attributes.stats?.ratingAverage?.toString()
                    ?: "N/A"),
                "Season" to (lit.attributes.publicationSeason ?: "Unknown"),
                "Chart" to (lit.attributes.stats?.rankGlobal?.toString() ?: "Unknown"),
                "Rated" to lit.attributes.tvRating.name,
                "Studio" to (lit.attributes.studio ?: "Unknown"),
                "Country" to (lit.attributes.countryOfOrigin?.name ?: "Unknown"),
            ),
            infos = listOfNotNull(
                lit.attributes.type.let {
                    InfoCard(
                        title = "Type",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                lit.attributes.source.let {
                    InfoCard(
                        title = "Source",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                lit.attributes.genres?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Genres",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                lit.attributes.themes?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Themes",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                lit.attributes.pageCount.let {
                    InfoCard(
                        title = "Pages",
                        value = it.toString(),
                        subtitle = ""
                    )
                },
                lit.attributes.duration.let {
                    InfoCard(
                        title = "Duration",
                        value = it,
                        subtitle = ""
                    )
                },
                lit.attributes.nextPublicationAt?.let {
                    InfoCard(
                        title = "Publication",
                        value = it.toString(),
                        subtitle = ""
                    )
                },
                lit.attributes.publicationTime?.let {
                    InfoCard(
                        title = "Published",
                        value = it,
                        subtitle = ""
                    )
                },
                lit.attributes.tvRating.let {
                    InfoCard(
                        title = "TV Rating",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                lit.attributes.countryOfOrigin?.let {
                    InfoCard(
                        title = "Country of Origin",
                        value = it.name,
                        subtitle = ""
                    )
                },
                lit.attributes.languages.takeIf { it.isNotEmpty() }?.let { langs ->
                    InfoCard(
                        title = "Languages",
                        value = langs.joinToString(", ") { lang -> lang.name },
                        subtitle = ""
                    )
                }
            ),
            mediaStat = lit.attributes.stats,
            status = lit.attributes.status,
            library = lit.attributes.library,
            reviews = reviews,
        )
    }
}
