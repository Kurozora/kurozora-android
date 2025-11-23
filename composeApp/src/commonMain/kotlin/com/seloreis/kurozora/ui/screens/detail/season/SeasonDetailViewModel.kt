package com.seloreis.kurozora.ui.screens.detail.season

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seloreis.kurozora.ui.screens.detail.SongDetailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.WatchStatus
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.show.Show
import kurozorakit.shared.Result

class SeasonDetailViewModel(
    private val kurozoraKit: KurozoraKit
) : ViewModel() {
    private val _state = MutableStateFlow(SeasonDetailState())
    val state: StateFlow<SeasonDetailState> = _state.asStateFlow()

    fun fetchSeasonEpisodes(seasonId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = kurozoraKit.season().getEpisodes(seasonId)

            if (result is Result.Success) {

                _state.update {
                    it.copy(
                        episodeIds = result.data.data.map { it.id },
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
    fun fetchEpisode(id: String) {
        if (_state.value.episodes.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }

            val res = kurozoraKit.episode().getEpisode(id)
            val episode: Episode? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (episode != null) {
                val updated = _state.value.episodes.toMutableMap()
                updated[id] = episode
                _state.update {
                    it.copy(episodes = updated, loadingItems = it.loadingItems - id)
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
                    val updatedMap = _state.value.episodes.mapValues { (id, ep) ->
                        if (id == episodeId) ep.copy(
                            attributes = ep.attributes.copy(
                                _watchStatus = result.data.data.watchStatus
                            )
                        ) else ep
                    }
                    _state.value = _state.value.copy(episodes = updatedMap)
                }

            } catch (e: Exception) {
                println("❌ Error in markAsWatchedEpisode: ${e.localizedMessage}")
            }
        }
    }
}