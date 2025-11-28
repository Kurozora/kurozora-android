package app.kurozora.ui.screens.detail

import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.review.Review
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show

data class EpisodeDetailState(
    val episode: Episode? = null,
    val seasonId: String? = null,
    val season: Season? = null,
    val showId: String? = null,
    val show: Show? = null,
    val previousEpisodeId: String? = null,
    val previousEpisode: Episode? = null,
    val nextEpisodeId: String? = null,
    val nextEpisode: Episode? = null,
    val episodeSuggestionsIds: List<String> = emptyList(),
    val episodeSuggestions: Map<String, Episode> = emptyMap(),
    val reviews: List<Review> = emptyList(),
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
