package app.kurozora.ui.screens.detail

import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.cast.Cast
import kurozorakit.data.models.show.related.RelatedGame
import kurozorakit.data.models.show.related.RelatedLiterature
import kurozorakit.data.models.show.related.RelatedShow
import kurozorakit.data.models.show.song.ShowSong
import kurozorakit.data.models.staff.Staff
import kurozorakit.data.models.studio.Studio

data class ShowDetailState(
    val show: Show? = null,
    val castIds: List<String> = emptyList(),
    val cast: Map<String, Cast> = emptyMap(),
    val characters: List<Character> = emptyList(),
    val relatedShows: List<RelatedShow> = emptyList(),
    val relatedLiteratures: List<RelatedLiterature> = emptyList(),
    val relatedGames: List<RelatedGame> = emptyList(),
    val seasonIds: List<String> = emptyList(),
    val seasons: Map<String, Season> = emptyMap(),
    val showSongs: List<ShowSong> = emptyList(),
    val staff: List<Staff> = emptyList(),
    val studioIds: List<String> = emptyList(),
    val studios: Map<String, Studio> = emptyMap(),
    val moreByStudioIds: List<String> = emptyList(),
    val moreByStudio: Map<String, Show> = emptyMap(),
    val loadingItems: Set<String> = emptySet(),
    val libraryStatus: KKLibrary.Status? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
