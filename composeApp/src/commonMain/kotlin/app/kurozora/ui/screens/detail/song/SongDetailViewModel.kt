package app.kurozora.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.show.Show
import kurozorakit.shared.Result

class SongDetailViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(SongDetailState())
    val state: StateFlow<SongDetailState> = _state.asStateFlow()
    fun fetchSongDetails(songId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.song().getSong(
                songId, listOf(
                    "shows", "games",
                )
            )

            val reviews = kurozoraKit.song().getSongReviews(songId)

            if (result is Result.Success) {
                val song = result.data.data.firstOrNull() ?: return@launch
                val relationships = song.relationships

                _state.update {
                    it.copy(
                        song = song,
                        showIds = relationships?.shows?.data?.map { it.id } ?: emptyList(),
                        gameIds = relationships?.games?.data?.map { it.id } ?: emptyList(),
                        reviews = reviews.getOrNull()?.data ?: emptyList(),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.toString()) }
            }
        }
    }

    /**
     * Lazy load: Show
     */
    fun fetchShow(id: String) {
        if (_state.value.shows.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.show().getShow(id)
            val show: Show? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (show != null) {
                val updated = _state.value.shows.toMutableMap()
                updated[id] = show
                _state.update {
                    it.copy(shows = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    /**
     * Lazy load: Game
     */
    fun fetchGame(id: String) {
        if (_state.value.games.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.game().getGame(id)
            val game: Game? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (game != null) {
                val updated = _state.value.games.toMutableMap()
                updated[id] = game
                _state.update {
                    it.copy(games = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun updateLibraryStatus(
        itemId: String,
        newStatus: KKLibrary.Status,
        type: ItemType,
        section: SectionType,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val kind = when (type) {
                    ItemType.Show -> KKLibrary.Kind.SHOWS
                    ItemType.Literature -> KKLibrary.Kind.LITERATURES
                    ItemType.Game -> KKLibrary.Kind.GAMES
                    else -> null
                }

                if (kind == null) {
                    println("⚠️ Unsupported type for library update: $type")
                    return@launch
                }
                val result = kurozoraKit.user().addToLibrary(kind, newStatus, itemId)

                if (result is Result.Success) {
                    println("✅ Library status updated for $type ($itemId) → $newStatus")

                    when (section) {
                        SectionType.RelatedShows -> {
                            val updatedShows = _state.value.shows.toMutableMap()
                            val show = updatedShows[itemId]
                            val updated = show?.copy(
                                attributes = show.attributes.copy(
                                    library = show.attributes.library?.copy(status = newStatus)
                                )
                            ) ?: show!!
                            updatedShows[itemId] = updated
                            _state.value = _state.value.copy(shows = updatedShows)
                        }

                        SectionType.RelatedGames -> {
                            val updatedGames = _state.value.games.toMutableMap()
                            val game = updatedGames[itemId]
                            val updated = game?.copy(
                                attributes = game.attributes.copy(
                                    library = game.attributes.library?.copy(status = newStatus)
                                )
                            ) ?: game!!
                            updatedGames[itemId] = updated
                            _state.value = _state.value.copy(games = updatedGames)
                        }
                        else -> {}
                    }
                } else {
                    println("⚠️ Failed to update library status for $itemId: $result")
                }
            } catch (e: Exception) {
                println("❌ Error updating library status: ${e.localizedMessage}")
            }
        }
    }
}
