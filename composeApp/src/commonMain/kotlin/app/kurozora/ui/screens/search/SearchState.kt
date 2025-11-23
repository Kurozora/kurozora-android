package app.kurozora.ui.screens.search

import app.kurozora.ui.components.cards.MediaCardViewMode
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchFilter
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.studio.Studio
import kurozorakit.data.models.user.User

data class SearchState(
    val searchItemIdentities: List<String> = emptyList(),
    val searchItems: Map<String, Any> = emptyMap(), // itemId -> item
    val query: String = "",
    val selectedTypes: Set<KKSearchType> = setOf(
        KKSearchType.shows,
        KKSearchType.literatures,
        KKSearchType.characters,
        KKSearchType.games,
        KKSearchType.episodes,
        KKSearchType.people,
        KKSearchType.songs,
        KKSearchType.studios,
        KKSearchType.users,
    ),
    val activeType: KKSearchType? = null,
    val activeFilter: Filterable? = null,
    val filter: KKSearchFilter? = null,
    val mediaCard: MediaCardViewMode = MediaCardViewMode.List,
    val columnCount: Int = 3,
    val sortType: KKLibrary.SortType = KKLibrary.SortType.NONE,
    val sortOption: KKLibrary.Option = KKLibrary.Option.NONE,
    // --------------------------
    val characterIds: List<String> = emptyList(),
    val episodeIds: List<String> = emptyList(),
    val gameIds: List<String> = emptyList(),
    val literatureIds: List<String> = emptyList(),
    val peopleIds: List<String> = emptyList(),
    val seasonIds: List<String> = emptyList(),
    val showIds: List<String> = emptyList(),
    val songIds: List<String> = emptyList(),
    val studioIds: List<String> = emptyList(),
    val userIds: List<String> = emptyList(),
    // --------------------------
    val characters: Map<String, Character> = emptyMap(),
    val episodes: Map<String, Episode> = emptyMap(),
    val games: Map<String, Game> = emptyMap(),
    val literatures: Map<String, Literature> = emptyMap(),
    val people: Map<String, Person> = emptyMap(),
    val seasons: Map<String, Season> = emptyMap(),
    val shows: Map<String, Show> = emptyMap(),
    val songs: Map<String, Song> = emptyMap(),
    val studios: Map<String, Studio> = emptyMap(),
    val users: Map<String, User> = emptyMap(),
    // -------------------------
    val isLoadingMore: Boolean = false,
    val characterNext: String? = null,
    val episodeNext: String? = null,
    val gameNext: String? = null,
    val literatureNext: String? = null,
    val showNext: String? = null,
    val peopleNext: String? = null,
    val seasonNext: String? = null,
    val songNext: String? = null,
    val studioNext: String? = null,
    val userNext: String? = null,
    // --------------------------
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
