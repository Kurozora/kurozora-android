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
import com.seloreis.kurozora.ui.components.cards.CharacterCard
import com.seloreis.kurozora.ui.components.cards.GameCard
import com.seloreis.kurozora.ui.components.cards.LiteratureCard
import com.seloreis.kurozora.ui.components.cards.PersonCard
import com.seloreis.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.show.Show
import org.koin.compose.viewmodel.koinViewModel
import kotlin.Any
import kotlin.Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    person: Person,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    viewModel: PersonDetailViewModel = koinViewModel(),
) {

    LaunchedEffect(Unit) {
        viewModel.fetchPersonDetails(person.id)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.attributes.fullName) },
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
                person.toDetailData(windowWidth)?.let { DetailContent(it,
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
                                    onClick = { onNavigateToItemDetail(show) },
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
                                    onClick = { onNavigateToItemDetail(literature) },
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

            if (state.characterIds.isNotEmpty()) {
                item { SectionHeader(title = "Characters") }
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



fun Person?.toDetailData(windowWidth: WindowWidthSizeClass): DetailData? {
    return this?.let { person ->
        DetailData(
            itemType = ItemType.Person,
            windowWidth = windowWidth,
            id = person.id,
            title = person.attributes.fullName,
            synopsis = person.attributes.about ?: "",
            synopsisTitle = "About",
            coverImageUrl = person.attributes.profile?.url ?: "",
            stats = mapOf(),
            infos = listOfNotNull(
                person.attributes.alternativeNames?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Aliases",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                person.attributes.age.let {
                    InfoCard(
                        title = "Age",
                        value = it ?: "-",
                        subtitle = ""
                    )
                },
                person.attributes.websiteURLs.let {
                    InfoCard(
                        title = "Websites",
                        value = it?.joinToString(", ") ?: "-",
                        subtitle = ""
                    )
                }
            ),
            mediaStat = person.attributes.stats,
        )
    }
}
