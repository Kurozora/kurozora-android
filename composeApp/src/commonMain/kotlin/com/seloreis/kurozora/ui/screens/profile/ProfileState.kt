package com.seloreis.kurozora.ui.screens.profile

import kurozorakit.data.enums.FollowStatus
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.user.User

data class ProfileState(
    val user: User? = null,
    val showsLibrary: List<Show> = emptyList(),
    val literaturesLibrary: List<Literature> = emptyList(),
    val gamesLibrary: List<Game> = emptyList(),
    val favoriteShows: List<Show> = emptyList(),
    val favoriteLiteratures: List<Literature> = emptyList(),
    val favoriteGames: List<Game> = emptyList(),
    val followStatus: FollowStatus? = null,
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
