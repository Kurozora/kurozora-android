package com.seloreis.kurozora.ui.screens.library

import com.seloreis.kurozora.ui.components.cards.MediaCardViewMode
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchFilter
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show

data class LibraryState(
    val selectedTab: LibraryTab = LibraryTab.Animes,
    val selectedStatus: KKLibrary.Status = KKLibrary.Status.INPROGRESS,
    // ------------------------------
    val query: String = "",
    val gameIds: List<String> = emptyList(),
    val literatureIds: List<String> = emptyList(),
    val showIds: List<String> = emptyList(),
    val games: Map<String, Game> = emptyMap(),
    val literatures: Map<String, Literature> = emptyMap(),
    val shows: Map<String, Show> = emptyMap(),
    val gameNext: String? = null,
    val literatureNext: String? = null,
    val showNext: String? = null,
    // ----------------------------
    val items: List<Any> = emptyList(),
    val mediaCard: MediaCardViewMode = MediaCardViewMode.Compact,
    val columnCount: Int = 4,
    val sortType: KKLibrary.SortType = KKLibrary.SortType.NONE,
    val sortOption: KKLibrary.Option = KKLibrary.Option.NONE,

    val activeType: KKSearchType? = null,
    val activeFilter: Filterable? = null,
    val filter: KKSearchFilter? = null,

    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class LibraryTab(val kind: KKLibrary.Kind) {
    Animes(KKLibrary.Kind.SHOWS),
    Mangas(KKLibrary.Kind.LITERATURES),
    Games(KKLibrary.Kind.GAMES);

    fun toSearchType(): KKSearchType {
        return when (this) {
            Animes -> KKSearchType.shows
            Mangas -> KKSearchType.literatures
            Games -> KKSearchType.games
        }
    }
}
