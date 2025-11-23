package com.seloreis.kurozora.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.SectionHeader
import com.seloreis.kurozora.ui.components.cards.AnimeCard
import com.seloreis.kurozora.ui.components.cards.CastCard
import com.seloreis.kurozora.ui.components.cards.GameCard
import com.seloreis.kurozora.ui.components.cards.LiteratureCard
import com.seloreis.kurozora.ui.components.cards.PersonCard
import com.seloreis.kurozora.ui.components.cards.SeasonCard
import com.seloreis.kurozora.ui.components.cards.SongCard
import com.seloreis.kurozora.ui.components.cards.StudioCard
import com.seloreis.kurozora.ui.screens.explore.ItemType
import com.seloreis.kurozora.ui.screens.list.show.RelatedShowsViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.studio.Studio
import org.koin.compose.viewmodel.koinViewModel

fun Instant.toReadableDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDate = this.toLocalDateTime(timeZone).date

    val shortMonths = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val month = shortMonths[localDate.monthNumber - 1] // monthNumber: 1..12
    val day = localDate.dayOfMonth
    val year = localDate.year

    return "$month $day, $year" // "Jun 5, 2025"
}

@Composable
fun ItemPlaceholder(
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    height: Dp = 200.dp
) {
    Box(
        modifier = modifier
            .size(width, height)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    show: Show,
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateToSeasonList: (String) -> Unit,
    onNavigateToCastList: (String) -> Unit,
    onNavigateToStaffList: (String) -> Unit,
    onNavigateToSongList: (String) -> Unit,
    onNavigateToStudioList: (String) -> Unit,
    onNavigateToMoreByStudioList: (String) -> Unit,
    onNavigateToRelatedShowList: (String) -> Unit,
    onNavigateToRelatedLiteratureList: (String) -> Unit,
    onNavigateToRelatedGameList: (String) -> Unit,
    viewModel: ShowDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.fetchShowDetails(show.id)
    }

    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(show.attributes?.title ?: "Loading...") },
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
            val detailShow = state.show ?: show
            item {
                detailShow.toDetailData(windowWidth)?.let {
                    DetailContent(
                        it,
                        onStatusSelected = { newStatus ->
                            viewModel.updateLibraryStatus(
                                state.show!!.id,
                                newStatus,
                                ItemType.Show,
                                SectionType.MainShow
                            )
                        },
                        onFavoriteClick = {
                            viewModel.updateFavoriteStatus(show.id)
                        },
                        onRemindClick = {
                            viewModel.updateReminderStatus(show.id)
                        },
                    )
                }
            }

            // Season
            if (state.seasonIds.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Season",
                        onSeeAllClick = { onNavigateToSeasonList(show.id) })
                }
                item {
                    ItemList(
                        items = state.seasonIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val season = state.seasons[id]
                            LaunchedEffect(id) { viewModel.fetchSeason(id) }

                            if (season != null) {
                                SeasonCard(season, onClick = { onNavigateToItemDetail(season) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            // Cast
            if (state.castIds.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Cast",
                        onSeeAllClick = { onNavigateToCastList(show.id) })
                }
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
            if (state.staff.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Staff",
                        onSeeAllClick = { onNavigateToStaffList(show.id) })
                }
                item {
                    ItemList(
                        items = state.staff,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { staff ->
                            val role = staff.attributes.role.name
                            val person = staff.relationships.person.data.first()
                            PersonCard(
                                person,
                                subTitle = role,
                                onClick = { onNavigateToItemDetail(person) })
                        }
                    )
                }
            }

            // Songs
            if (state.showSongs.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Songs",
                        onSeeAllClick = { onNavigateToSongList(show.id) })
                }
                item {
                    ItemList(
                        items = state.showSongs,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { showSong ->
                            val song = showSong.song
                            SongCard(song, onClick = { onNavigateToItemDetail(song) })
                        }
                    )
                }
            }

            // Studios
            if (state.studioIds.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Studios",
                        onSeeAllClick = { onNavigateToStudioList(show.id) })
                }
                item {
                    ItemList(
                        items = state.studioIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val studio = state.studios[id]
                            LaunchedEffect(id) { viewModel.fetchStudio(id) }

                            if (studio != null) {
                                StudioCard(
                                    studio = studio,
                                    onClick = { onNavigateToItemDetail(studio) })
                            } else {
                                ItemPlaceholder()
                            }
                        }
                    )
                }
            }

            // More by Studio
            if (state.moreByStudioIds.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "More By Studio",
                        onSeeAllClick = { onNavigateToMoreByStudioList(show.id) })
                }
                item {
                    ItemList(
                        items = state.moreByStudioIds,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { id ->
                            val show = state.moreByStudio[id]
                            LaunchedEffect(id) { viewModel.fetchMoreByStudioShow(id) }

                            if (show != null) {
                                AnimeCard(
                                    show,
                                    onClick = { onNavigateToItemDetail(show) },
                                    onStatusSelected = { newStatus ->
                                        viewModel.updateLibraryStatus(
                                            show.id,
                                            newStatus,
                                            ItemType.Show,
                                            SectionType.MoreByStudio
                                        )
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
                item {
                    SectionHeader(
                        title = "Related Shows",
                        onSeeAllClick = { onNavigateToRelatedShowList(show.id) })
                }
                item {
                    ItemList(
                        items = state.relatedShows,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { related ->
                            val show = related.show
                            val relation = related.attributes.relation.name
                            AnimeCard(
                                show,
                                topTitle = relation,
                                onClick = { onNavigateToItemDetail(show) },
                                onStatusSelected = { newStatus ->
                                    viewModel.updateLibraryStatus(
                                        show.id,
                                        newStatus,
                                        ItemType.Show,
                                        SectionType.RelatedShows
                                    )
                                }
                            )
                        }
                    )
                }
            }

            // Related Literatures
            if (state.relatedLiteratures.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Related Literatures",
                        onSeeAllClick = { onNavigateToRelatedLiteratureList(show.id) })
                }
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
                                    viewModel.updateLibraryStatus(
                                        lit.id,
                                        newStatus,
                                        ItemType.Literature,
                                        SectionType.RelatedLiteratures
                                    )
                                }
                            )
                        }
                    )
                }
            }

            // Related Games
            if (state.relatedGames.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Related Games",
                        onSeeAllClick = { onNavigateToRelatedGameList(show.id) })
                }
                item {
                    ItemList(
                        items = state.relatedGames,
                        viewMode = ItemListViewMode.Horizontal,
                        itemContent = { related ->
                            val game = related.game
                            val relation = related.attributes.relation.name
                            GameCard(
                                game,
                                onClick = { onNavigateToItemDetail(game) },
                                onStatusSelected = { newStatus ->
                                    viewModel.updateLibraryStatus(
                                        game.id,
                                        newStatus,
                                        ItemType.Game,
                                        SectionType.RelatedGames
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}


fun Show?.toDetailData(windowWidth: WindowWidthSizeClass): DetailData? {
    return this?.let { show ->
        DetailData(
            itemType = ItemType.Show,
            windowWidth = windowWidth,
            id = show.id,
            title = show.attributes.title,
            synopsis = show.attributes.synopsis ?: "",
            coverImageUrl = show.attributes.poster?.url ?: "",
            bannerImageUrl = show.attributes.banner?.url,
            stats = mapOf(
                (show.attributes.stats?.ratingCount?.toString()
                    .orEmpty() + " reviews") to (show.attributes.stats?.ratingAverage?.toString()
                    ?: "N/A"),
                "Season" to (show.attributes.airSeason ?: "Unknown"),
                "Chart" to (show.attributes.stats?.rankGlobal?.toString() ?: "Unknown"),
                "Rated" to show.attributes.tvRating.name,
                "Studio" to (show.attributes.studio ?: "Unknown"),
                "Country" to (show.attributes.countryOfOrigin?.name ?: "Unknown"),
                (show.attributes.languages.size.toString() + " More Languages") to (show.attributes.languages.firstOrNull()?.name
                    ?: "Unknown")
            ),
            infos = listOfNotNull(
                show.attributes.type.let {
                    InfoCard(
                        title = "Type",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                show.attributes.source.let {
                    InfoCard(
                        title = "Source",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                show.attributes.genres?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Genres",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                show.attributes.themes?.takeIf { it.isNotEmpty() }?.let {
                    InfoCard(
                        title = "Themes",
                        value = it.joinToString(", "),
                        subtitle = ""
                    )
                },
                show.attributes.episodeCount.let {
                    InfoCard(
                        title = "Episodes",
                        value = it.toString(),
                        subtitle = ""
                    )
                },
                show.attributes.duration.let {
                    InfoCard(
                        title = "Duration",
                        value = it,
                        subtitle = ""
                    )
                },
                show.attributes.nextBroadcastAt?.let {
                    InfoCard(
                        title = "Broadcast",
                        value = it.toReadableDate(),
                        subtitle = ""
                    )
                },
                show.attributes.airTime?.let {
                    InfoCard(
                        title = "Aired",
                        value = it.toReadableDate(),
                        subtitle = ""
                    )
                },
                show.attributes.tvRating.let {
                    InfoCard(
                        title = "TV Rating",
                        value = it.name,
                        subtitle = it.description
                    )
                },
                show.attributes.countryOfOrigin?.let {
                    InfoCard(
                        title = "Country of Origin",
                        value = it.name,
                        subtitle = ""
                    )
                },
                show.attributes.languages.takeIf { it.isNotEmpty() }?.let { langs ->
                    InfoCard(
                        title = "Languages",
                        value = langs.joinToString(", ") { lang -> lang.name },
                        subtitle = ""
                    )
                }
            ),
            mediaStat = show.attributes.stats,
            status = show.attributes.status,
            library = show.attributes.library
        )
    }
}
