package com.seloreis.kurozora.ui.screens.detail

import kurozorakit.data.models.character.Character
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.show.Show

data class PersonDetailState(
    val person: Person? = null,
    val characterIds: List<String> = emptyList(),
    val characters: Map<String, Character> = emptyMap(),
    val showIds: List<String> = emptyList(),
    val shows: Map<String, Show> = emptyMap(),
    val literatureIds: List<String> = emptyList(),
    val literatures: Map<String, Literature> = emptyMap(),
    val gameIds: List<String> = emptyList(),
    val games: Map<String, Game> = emptyMap(),
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)