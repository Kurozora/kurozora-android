package com.seloreis.kurozora.ui.screens.detail

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
import com.seloreis.kurozora.ui.components.cards.GameCard
import com.seloreis.kurozora.ui.components.cards.LiteratureCard
import com.seloreis.kurozora.ui.components.cards.PersonCard
import com.seloreis.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.studio.Studio
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioDetailScreen(
    studio: Studio,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetails: (Show) -> Unit,
    onNavigateToMangaDetails: (Literature) -> Unit,
    viewModel: StudioDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchStudioDetails(studio.id)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(studio.attributes.name) },
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
                studio.toDetailData(windowWidth)?.let { DetailContent(it,
                    onStatusSelected = { newStatus ->
                        //viewModel.updateLibraryStatus(state.show!!.id, newStatus, ItemType.Show, SectionType.MainShow)
                    }) }
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

            if (state.literatureIds.isNotEmpty()) {
                item { SectionHeader(title = "Manga") }
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
                                    onClick = { onNavigateToMangaDetails(literature) },
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
                                    onClick = {  },
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


fun Studio?.toDetailData(windowWidth: WindowWidthSizeClass): DetailData? {
    return this?.let { studio ->
        DetailData(
            itemType = ItemType.Studio,
            windowWidth = windowWidth,
            id = studio.id,
            title = studio.attributes.name,
            synopsis = studio.attributes.about ?: "",
            synopsisTitle = "About",
            coverImageUrl = studio.attributes.profile?.url ?: "",
            stats = mapOf(
                (studio.attributes.stats?.ratingCount?.toString()
                    .orEmpty() + " reviews") to (studio.attributes.stats?.ratingAverage?.toString()
                    ?: "N/A"),
                "Chart" to (studio.attributes.stats?.rankGlobal?.toString() ?: "Unknown"),
                "Rated" to (studio.attributes.tvRating.name),
            ),
            infos = listOfNotNull(
                studio.attributes.alternativeNames?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Aliases",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                studio.attributes.foundedAtTimestamp.let {
                    InfoCard(
                        title = "Founded",
                        value = it.toString(),
                        subtitle = ""
                    )
                },
                studio.attributes.defunctAtTimestamp.let {
                    InfoCard(
                        title = "Defunct",
                        value = it.toString(),
                        subtitle = ""
                    )
                },studio.attributes.let {
                    InfoCard(
                        title = "Headquarters",
                        value = "-",
                        subtitle = ""
                    )
                },studio.attributes.tvRating.let {
                    InfoCard(
                        title = "Rating",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                studio.attributes.socialURLs.let {
                    InfoCard(
                        title = "Socials",
                        value = it?.joinToString(", ") ?: "-",
                        subtitle = ""
                    )
                },
                studio.attributes.websiteURLs.let {
                    InfoCard(
                        title = "Websites",
                        value = it?.joinToString(", ") ?: "-",
                        subtitle = ""
                    )
                },
            ),
            mediaStat = studio.attributes.stats,
        )
    }
}
