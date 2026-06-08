package app.kurozora.ui.screens.recap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.components.cards.parseColor
import app.kurozora.ui.screens.recap.components.RecapGenreItem
import app.kurozora.ui.screens.recap.components.RecapMediaItem
import app.kurozora.ui.screens.recap.components.RecapScreenData
import app.kurozora.ui.screens.recap.components.RecapThemeItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit

class RecapItemViewModel(private val kurozoraKit: KurozoraKit) : ViewModel() {
    private val _state = MutableStateFlow(RecapItemState())
    val state: StateFlow<RecapItemState> = _state.asStateFlow()

    fun fetchRecapDetails(year: String, month: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            kurozoraKit.user().getMyRecapDetails(year = year, month = month)
                .onSuccess { res ->
                    val items = res.data

                    val showIds = items.find { it.attributes.type == "shows" }?.relationships?.shows?.data?.map { it.id } ?: emptyList()
                    val litIds = items.find { it.attributes.type == "literatures" }?.relationships?.literatures?.data?.map { it.id } ?: emptyList()
                    val genreIds = items.find { it.attributes.type == "genres" }?.relationships?.genres?.data?.map { it.id } ?: emptyList()
                    val themeIds = items.find { it.attributes.type == "themes" }?.relationships?.themes?.data?.map { it.id } ?: emptyList()

                    val mainStats = items.find { it.attributes.type == "shows" } ?: items.first()

                    coroutineScope {
                        // 1. Tüm request'leri paralel başlat (List<Deferred>)
                        val showsDeferred = showIds.map { id -> async { kurozoraKit.show().getShow(id).getOrNull() } }
                        val litsDeferred = litIds.map { id -> async { kurozoraKit.literature().getLiterature(id).getOrNull() } }
                        val genreDeferred = genreIds.map { id -> async { kurozoraKit.genre().getGenre(genreId = id).getOrNull() } }
                        val themeDeferred = themeIds.map { id -> async { kurozoraKit.theme().getTheme(themeId = id).getOrNull() } }

                        // 2. awaitAll() ile tüm sonuçların tamamlanmasını bekle
                        // filterNotNull() kullanarak sadece başarılı dönen verileri alıyoruz
                        val showsResponses = showsDeferred.awaitAll().filterNotNull()
                        val litsResponses = litsDeferred.awaitAll().filterNotNull()
                        val genresResponses = genreDeferred.awaitAll().filterNotNull()
                        val themesResponses = themeDeferred.awaitAll().filterNotNull()

                        val finalData = RecapScreenData(
                            year = year.toInt(),
                            type = mainStats.attributes.type.uppercase(),
                            totalSeriesCount = mainStats.attributes.totalSeriesCount,
                            totalPartsCount = mainStats.attributes.totalPartsCount,
                            totalPartsDuration = mainStats.attributes.totalPartsDuration,
                            topPercentile = mainStats.attributes.topPercentile.toDouble(),

                            topShows = showsResponses.flatMap { it.data }.mapIndexed { index, show ->
                                RecapMediaItem(
                                    rank = index + 1,
                                    title = show.attributes.title,
                                    subtitle = show.attributes.tagline ?: show.attributes.originalTitle ?: "",
                                    posterUrl = show.attributes.poster?.url,
                                    accentColor = parseColor(show.attributes.poster?.backgroundColor.orEmpty())
                                )
                            },
                            topLiteratures = litsResponses.flatMap { it.data }.mapIndexed { index, lit ->
                                RecapMediaItem(
                                    rank = index + 1,
                                    title = lit.attributes.title,
                                    subtitle = lit.attributes.tagline ?: lit.attributes.originalTitle ?: "",
                                    posterUrl = lit.attributes.poster?.url,
                                    accentColor = parseColor(lit.attributes.poster?.backgroundColor.orEmpty())
                                )
                            },
                            topGenres = genresResponses.flatMap { it.data }.map { genre ->
                                RecapGenreItem(
                                    name = genre.attributes.name,
                                    symbol = genre.attributes.symbol?.url.orEmpty(),
                                    color1 = parseColor(genre.attributes.backgroundColor1),
                                    color2 = parseColor(genre.attributes.backgroundColor2)
                                )
                            },
                            topThemes = themesResponses.flatMap { it.data }.map { t ->
                                RecapThemeItem(
                                    name = t.attributes.name,
                                    symbol = t.attributes.symbol?.url.orEmpty(),
                                    color1 = parseColor(t.attributes.backgroundColor1),
                                    color2 = parseColor(t.attributes.backgroundColor2)
                                )
                            }
                        )

                        _state.update { it.copy(recapData = finalData, isLoading = false) }
                    }
                }
                .onError { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }
}