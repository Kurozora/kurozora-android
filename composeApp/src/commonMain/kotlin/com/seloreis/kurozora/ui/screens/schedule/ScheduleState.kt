package com.seloreis.kurozora.ui.screens.schedule

import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show

data class ScheduleState(
    val shows: List<Show> = emptyList(),
    val games: List<Game> = emptyList(),
    val literatures: List<Literature> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)