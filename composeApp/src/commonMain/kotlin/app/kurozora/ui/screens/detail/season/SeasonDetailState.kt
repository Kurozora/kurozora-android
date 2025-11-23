package app.kurozora.ui.screens.detail.season

import kurozorakit.data.models.episode.Episode

data class SeasonDetailState(
    //val season: Season,
    val episodeIds: List<String> = emptyList(),
    val episodes: Map<String, Episode> = emptyMap(),
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
