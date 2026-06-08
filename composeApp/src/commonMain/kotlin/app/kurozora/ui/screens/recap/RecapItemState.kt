package app.kurozora.ui.screens.recap

import app.kurozora.ui.screens.recap.components.RecapGenreItem
import app.kurozora.ui.screens.recap.components.RecapMediaItem
import app.kurozora.ui.screens.recap.components.RecapScreenData
import app.kurozora.ui.screens.recap.components.RecapThemeItem
import kurozorakit.data.models.recap.item.RecapItem

data class RecapItemState(
    val recapItems: List<RecapItem> = emptyList(),
    val recapData: RecapScreenData? = null,
    val topShows: List<RecapMediaItem> = emptyList(),
    val topLiteratures: List<RecapMediaItem> = emptyList(),
    val topGenres: List<RecapGenreItem> = emptyList(),
    val topThemes: List<RecapThemeItem> = emptyList(),


    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)