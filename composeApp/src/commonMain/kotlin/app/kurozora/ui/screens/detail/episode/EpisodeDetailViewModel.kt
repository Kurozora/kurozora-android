package app.kurozora.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.episode.Episode
import kurozorakit.shared.Result

class EpisodeDetailViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(EpisodeDetailState())
    val state: StateFlow<EpisodeDetailState> = _state.asStateFlow()
    fun fetchEpisodeDetails(episodeId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.episode().getEpisode(
                episodeId, listOf(
                    "season", "show",
                )
            )
            val episodeSuggestions = kurozoraKit.episode().getEpisodeSuggestions(episodeId)

            if (result is Result.Success) {
                val episode = result.data.data.firstOrNull() ?: return@launch
                val relationships = episode.relationships

                _state.update {
                    it.copy(
                        episode = episode,
                        seasonId = relationships?.seasons?.data?.firstOrNull()?.id,
                        showId = relationships?.shows?.data?.firstOrNull()?.id,
                        previousEpisodeId = relationships?.previousEpisodes?.data?.firstOrNull()?.id,
                        nextEpisodeId = relationships?.nextEpisodes?.data?.firstOrNull()?.id,
                        episodeSuggestionsIds = episodeSuggestions.getOrNull()?.data?.map { it.id }
                            .orEmpty(),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.toString()) }
            }
        }
    }

    /**
     * Lazy load: Episode
     */
    fun fetchSuggestedEpisode(id: String) {
        if (_state.value.episodeSuggestions.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.episode().getEpisode(id)
            val episode: Episode? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (episode != null) {
                val updated = _state.value.episodeSuggestions.toMutableMap()
                updated[id] = episode
                _state.update {
                    it.copy(episodeSuggestions = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun markEpisodeAsWatched(episodeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)

                if (result is Result.Success) {
                    if (episodeId == _state.value.episode?.id) {
                        val episode = _state.value.episode
                        val updated = episode?.copy(
                            attributes = episode.attributes.copy(
                                _watchStatus = result.data.data.watchStatus
                            )
                        )
                        _state.value = _state.value.copy(episode = updated)
                    } else {
                        val updatedMap = _state.value.episodeSuggestions.mapValues { (id, ep) ->
                            if (id == episodeId) ep.copy(
                                attributes = ep.attributes.copy(
                                    _watchStatus = result.data.data.watchStatus
                                )
                            ) else ep
                        }
                        _state.value = _state.value.copy(episodeSuggestions = updatedMap)
                    }
                }
            } catch (e: Exception) {
                println("❌ Error in markAsWatchedEpisode: ${e.localizedMessage}")
            }
        }
    }
}
