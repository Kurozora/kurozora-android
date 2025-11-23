package com.seloreis.kurozora.ui.screens.detail

import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.cast.Cast
import kurozorakit.data.models.show.related.RelatedGame
import kurozorakit.data.models.show.related.RelatedLiterature
import kurozorakit.data.models.show.related.RelatedShow
import kurozorakit.data.models.show.song.ShowSong
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.staff.Staff
import kurozorakit.data.models.studio.Studio

data class LiteratureDetailState(
    val literature: Literature? = null,
    val castIds: List<String> = emptyList(),
    val cast: Map<String, Cast> = emptyMap(),
    val characterIds: List<String> = emptyList(),
    val characters: Map<String, Character> = emptyMap(),
    val peopleIds: List<String> = emptyList(),
    val people: Map<String, Person> = emptyMap(),
    val relatedShows: List<RelatedShow> = emptyList(),
    val relatedLiteratures: List<RelatedLiterature> = emptyList(),
    val relatedGames: List<RelatedGame> = emptyList(),
    val songIds: List<String> = emptyList(),
    val songs: Map<String, Song> = emptyMap(),
    val staffIds: List<String> = emptyList(),
    val staff: Map<String, Staff> = emptyMap(),
    val studioIds: List<String> = emptyList(),
    val studios: Map<String, Studio> = emptyMap(),
    val moreByStudioIds: List<String> = emptyList(),
    val moreByStudio: Map<String, Literature> = emptyMap(),
    val loadingItems: Set<String> = emptySet(),
    val libraryStatus: KKLibrary.Status? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
