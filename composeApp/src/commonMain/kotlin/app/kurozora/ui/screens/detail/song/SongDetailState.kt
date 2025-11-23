package app.kurozora.ui.screens.detail

import kurozorakit.data.models.game.Game
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song

data class SongDetailState(
    val song: Song? = null,
    val showIds: List<String> = emptyList(),
    val shows: Map<String, Show> = emptyMap(),
    val gameIds: List<String> = emptyList(),
    val games: Map<String, Game> = emptyMap(),
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

