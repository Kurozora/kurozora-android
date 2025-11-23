package com.seloreis.kurozora.ui.screens.favorite

import com.seloreis.kurozora.ui.screens.library.LibraryTab
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show

data class FavoriteState(
    val selectedTab: LibraryTab = LibraryTab.Animes,
    val shows: List<Show> = emptyList(),
    val games: List<Game> = emptyList(),
    val literatures: List<Literature> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)