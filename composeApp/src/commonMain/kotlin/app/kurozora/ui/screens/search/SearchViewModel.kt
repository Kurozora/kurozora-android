package app.kurozora.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.components.cards.MediaCardViewMode
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchFilter
import kurozorakit.data.enums.KKSearchScope
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.search.Search
import kurozorakit.data.models.search.filters.CharacterFilter
import kurozorakit.data.models.search.filters.EpisodeFilter
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.PersonFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.SongFilter
import kurozorakit.data.models.search.filters.StudioFilter
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.studio.Studio
import kurozorakit.data.models.user.User
import kurozorakit.shared.Result

class SearchViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    /** 🔍 Normal arama — tüm typeları arar */
    fun search(query: String) {
        _state.update { it.copy(query = query /* activeType = null*/) }
        performSearch(query, allTypes())
    }

    /** 🔍 Sadece belirli type’a göre arama */
    fun searchByType(type: KKSearchType, query: String) {
        _state.update { it.copy(query = query, activeType = type) }
        performSearch(query, listOf(type))
    }

    /** ⬅️ Geri dön — tüm sonuçları göster */
    fun clearActiveType() {
        val q = _state.value.query
        _state.update { it.copy(activeType = null) }
        performSearch(q, allTypes())
    }

    fun toggleType(type: KKSearchType) {
        _state.update {
            val newSelected = it.selectedTypes.toMutableSet().apply {
                if (contains(type)) remove(type) else add(type)
            }
            it.copy(selectedTypes = newSelected)
        }
    }

    /** 🧩 Arama işlemi */
    private fun performSearch(query: String, types: List<KKSearchType>, filter: KKSearchFilter? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            kurozoraKit.search().search(
                scope = KKSearchScope.kurozora,
                types = types,
                query = query,
                filter = filter
            ).onSuccess { res ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        characterIds = res.data.characters?.data?.map { it.id } ?: emptyList(),
                        episodeIds = res.data.episodes?.data?.map { it.id } ?: emptyList(),
                        gameIds = res.data.games?.data?.map { it.id } ?: emptyList(),
                        showIds = res.data.shows?.data?.map { it.id } ?: emptyList(),
                        literatureIds = res.data.literatures?.data?.map { it.id } ?: emptyList(),
                        peopleIds = res.data.people?.data?.map { it.id } ?: emptyList(),
                        seasonIds = res.data.seasons?.data?.map { it.id } ?: emptyList(),
                        songIds = res.data.songs?.data?.map { it.id } ?: emptyList(),
                        studioIds = res.data.studios?.data?.map { it.id } ?: emptyList(),
                        userIds = res.data.users?.data?.map { it.id } ?: emptyList(),
                        // ----------------------------------------------
                        characterNext = res.data.characters?.next,
                        episodeNext = res.data.episodes?.next,
                        gameNext = res.data.games?.next,
                        literatureNext = res.data.literatures?.next,
                        showNext = res.data.shows?.next,
                        peopleNext = res.data.people?.next,
                        songNext = res.data.songs?.next,
                        studioNext = res.data.studios?.next,
                        userNext = res.data.users?.next,
                    )
                }
            }.onError { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun updateFilter(filter: Filterable) {
        val f = when (filter) {
            is ShowFilter -> KKSearchFilter.Show(filter)
            is LiteratureFilter -> KKSearchFilter.Literature(filter)
            is CharacterFilter -> KKSearchFilter.Character(filter)
            is EpisodeFilter -> KKSearchFilter.Episode(filter)
            is GameFilter -> KKSearchFilter.Game(filter)
            is StudioFilter -> KKSearchFilter.Studio(filter)
            is PersonFilter -> KKSearchFilter.Person(filter)
            is SongFilter -> KKSearchFilter.Song(filter)
            else -> KKSearchFilter.Show(filter as ShowFilter)
        }
        println(filter)
        _state.update { it.copy(filter = f) }
    }

    fun applyFilter() {
        val current = _state.value
        val query = current.query
        val type = current.activeType
        if (type != null) {
            performSearch(query, listOf(type), current.filter)
        } else {
            //performSearch(query, allTypes(), current.activeFilter)
        }
    }

    fun applySort(sortType: KKLibrary.SortType, sortOption: KKLibrary.Option) {
        _state.update { it.copy(sortType = sortType, sortOption = sortOption) }
    }

    fun updateCardViewMode(mode: MediaCardViewMode) {
        _state.value = _state.value.copy(mediaCard = mode)
    }

    fun updateColumnCount(count: Int) {
        _state.value = _state.value.copy(columnCount = count)
    }

    private fun allTypes() = listOf(
        KKSearchType.shows,
        KKSearchType.literatures,
        KKSearchType.characters,
        KKSearchType.games,
        KKSearchType.episodes,
        KKSearchType.people,
        KKSearchType.songs,
        KKSearchType.studios,
        //KKSearchType.seasons,
        KKSearchType.users,
    )

    fun loadMore(type: KKSearchType) {
        val next = when (type) {
            KKSearchType.characters -> _state.value.characterNext
            KKSearchType.episodes -> _state.value.episodeNext
            KKSearchType.games -> _state.value.gameNext
            KKSearchType.literatures -> _state.value.literatureNext
            KKSearchType.shows -> _state.value.showNext
            KKSearchType.people -> _state.value.peopleNext
            KKSearchType.songs -> _state.value.songNext
            KKSearchType.studios -> _state.value.studioNext
            KKSearchType.users -> _state.value.userNext
        } ?: return // next yoksa çık

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }

            kurozoraKit.search().search(
                scope = KKSearchScope.kurozora,
                types = listOf(),
                query = "",
                next = next.removePrefix("/v1/")
            ).onSuccess { res ->
                println("helo")
                _state.update {
                    val oldIds = when (type) {
                        KKSearchType.characters -> it.characterIds
                        KKSearchType.episodes -> it.episodeIds
                        KKSearchType.games -> it.gameIds
                        KKSearchType.literatures -> it.literatureIds
                        KKSearchType.shows -> it.showIds
                        KKSearchType.people -> it.peopleIds
                        KKSearchType.songs -> it.songIds
                        KKSearchType.studios -> it.studioIds
                        KKSearchType.users -> it.userIds
                    }
                    val newIds = res.data.allIdentities() // tüm yeni id’leri getir
                    val nextValue = res.data.characters?.next
                        ?: // örnek, her tip ayrı kontrol edilmeli
                        res.data.episodes?.next ?: res.data.games?.next
                        ?: res.data.literatures?.next ?: res.data.shows?.next
                        ?: res.data.people?.next ?: res.data.songs?.next ?: res.data.studios?.next
                        ?: res.data.users?.next

                    when (type) {
                        KKSearchType.characters -> it.copy(characterIds = oldIds + newIds, characterNext = nextValue)
                        KKSearchType.episodes -> it.copy(episodeIds = oldIds + newIds, episodeNext = nextValue)
                        KKSearchType.games -> it.copy(gameIds = oldIds + newIds, gameNext = nextValue)
                        KKSearchType.literatures -> it.copy(literatureIds = oldIds + newIds, literatureNext = nextValue)
                        KKSearchType.shows -> it.copy(showIds = oldIds + newIds, showNext = nextValue)
                        KKSearchType.people -> it.copy(peopleIds = oldIds + newIds, peopleNext = nextValue)
                        KKSearchType.songs -> it.copy(songIds = oldIds + newIds, songNext = nextValue)
                        KKSearchType.studios -> it.copy(studioIds = oldIds + newIds, studioNext = nextValue)
                        KKSearchType.users -> it.copy(userIds = oldIds + newIds, userNext = nextValue)
                    }
                }
            }.onError { error ->
                _state.update { it.copy(isLoadingMore = false, errorMessage = error.message) }
            }
        }
    }

    fun fetchCharacter(id: String) {
        if (_state.value.characters.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.character().getCharacter(id)
            val character: Character? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (character != null) {
                val updated = _state.value.characters.toMutableMap()
                updated[id] = character
                _state.update {
                    it.copy(characters = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchEpisode(id: String) {
        if (_state.value.episodes.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.episode().getEpisode(id)
            val episode: Episode? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (episode != null) {
                val updated = _state.value.episodes.toMutableMap()
                updated[id] = episode
                _state.update {
                    it.copy(episodes = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchGame(id: String) {
        if (_state.value.games.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.game().getGame(id)
            val game: Game? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (game != null) {
                val updated = _state.value.games.toMutableMap()
                updated[id] = game
                _state.update {
                    it.copy(games = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchLiterature(id: String) {
        if (_state.value.literatures.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.literature().getLiterature(id)
            val literature: Literature? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (literature != null) {
                val updated = _state.value.literatures.toMutableMap()
                updated[id] = literature
                _state.update {
                    it.copy(literatures = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchPerson(id: String) {
        if (_state.value.people.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.people().getPerson(id)
            val person: Person? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (person != null) {
                val updated = _state.value.people.toMutableMap()
                updated[id] = person
                _state.update {
                    it.copy(people = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchSeason(id: String) {
        if (_state.value.seasons.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.season().getDetails(id)
            val season: Season? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (season != null) {
                val updated = _state.value.seasons.toMutableMap()
                updated[id] = season
                _state.update {
                    it.copy(seasons = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchShow(id: String) {
        if (_state.value.shows.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.show().getShow(id)
            val show: Show? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (show != null) {
                val updated = _state.value.shows.toMutableMap()
                updated[id] = show
                _state.update {
                    it.copy(shows = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchSong(id: String) {
        if (_state.value.songs.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.song().getSong(id)
            val song: Song? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (song != null) {
                val updated = _state.value.songs.toMutableMap()
                updated[id] = song
                _state.update {
                    it.copy(songs = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchStudio(id: String) {
        if (_state.value.studios.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.studio().getStudio(id)
            val studio: Studio? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (studio != null) {
                val updated = _state.value.studios.toMutableMap()
                updated[id] = studio
                _state.update {
                    it.copy(studios = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun fetchUser(id: String) {
        if (_state.value.users.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.auth().getUserProfile(id)
            val user: User? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (user != null) {
                val updated = _state.value.users.toMutableMap()
                updated[id] = user
                _state.update {
                    it.copy(users = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    fun updateLibraryStatus(
        itemId: String,
        newStatus: KKLibrary.Status,
        type: ItemType,
    ) {
        viewModelScope.launch {
            // 1) Type → Kind çevir
            val kind = when (type) {
                ItemType.Show -> KKLibrary.Kind.SHOWS
                ItemType.Literature -> KKLibrary.Kind.LITERATURES
                ItemType.Game -> KKLibrary.Kind.GAMES
                else -> null
            }

            if (kind == null) return@launch

            try {
                // 2) API çağrısı
                val result = kurozoraKit.user().addToLibrary(kind, newStatus, itemId)

                if (result !is Result.Success) {
                    println("⚠️ Failed to update status ($itemId → $newStatus): $result")
                    return@launch
                }

                println("✅ Library updated ($itemId → $newStatus)")

                _state.update { state ->
                    when (type) {
                        ItemType.Show -> {
                            state.shows[itemId]?.let { show ->
                                val updated = show.copy(
                                    attributes = show.attributes.copy(
                                        library = show.attributes.library?.copy(status = newStatus)
                                    )
                                )

                                state.copy(
                                    shows = state.shows + (itemId to updated)
                                )
                            } ?: state
                        }

                        ItemType.Game -> {
                            state.games[itemId]?.let { game ->
                                val updated = game.copy(
                                    attributes = game.attributes.copy(
                                        library = game.attributes.library?.copy(status = newStatus)
                                    )
                                )

                                state.copy(
                                    games = state.games + (itemId to updated)
                                )
                            } ?: state
                        }

                        ItemType.Literature -> {
                            state.literatures[itemId]?.let { lit ->
                                val updated = lit.copy(
                                    attributes = lit.attributes.copy(
                                        library = lit.attributes.library?.copy(status = newStatus)
                                    )
                                )

                                state.copy(
                                    literatures = state.literatures + (itemId to updated)
                                )
                            } ?: state
                        }

                        else -> state
                    }
                }
            } catch (e: Exception) {
                println("❌ updateLibraryStatus error: ${e.localizedMessage}")
            }
        }
    }

    fun markEpisodeAsWatched(episodeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1️⃣ API çağrısı
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)
                if (result !is Result.Success) {
                    println("⚠️ Failed to mark episode as watched: $result")
                    return@launch
                }
                val watchStatus = result.data.data.watchStatus
                println("✅ Episode marked as watched: $episodeId → Watch Status: $watchStatus")
                // 2️⃣ Eğer state.episodes map’inde varsa güncelle
                _state.update { state ->
                    val currentEpisode = state.episodes[episodeId]
                    if (currentEpisode == null) return@update state
                    val updatedEpisode = currentEpisode.copy(
                        attributes = currentEpisode.attributes.copy(
                            _watchStatus = watchStatus
                        )
                    )

                    state.copy(
                        episodes = state.episodes + (episodeId to updatedEpisode)
                    )
                }
            } catch (e: Exception) {
                println("❌ Error marking episode watched: ${e.localizedMessage}")
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.auth().updateFollowStatus(userId)
                if (result !is Result.Success) {
                    println("⚠️ Failed to update follow status for $userId: $result")
                    return@launch
                }
                val newStatus = result.data.data.followStatus
                println("✅ Follow status updated for $userId → $newStatus")
                // 1️⃣ Eğer state.users map’inde varsa güncelle
                _state.update { state ->
                    val currentUser = state.users[userId] ?: return@update state
                    val updatedUser = currentUser.copy(
                        attributes = currentUser.attributes.copy(
                            _followStatus = newStatus
                        )
                    )

                    state.copy(
                        users = state.users + (userId to updatedUser)
                    )
                }
            } catch (e: Exception) {
                println("❌ Error followUser: ${e.localizedMessage}")
            }
        }
    }
}

fun Search.allIdentities(): List<String> {
    val ids = mutableListOf<String>()
    characters?.data?.mapTo(ids) { it.id }
    shows?.data?.mapTo(ids) { it.id }
    games?.data?.mapTo(ids) { it.id }
    episodes?.data?.mapTo(ids) { it.id }
    literatures?.data?.mapTo(ids) { it.id }
    people?.data?.mapTo(ids) { it.id }
    seasons?.data?.mapTo(ids) { it.id }
    songs?.data?.mapTo(ids) { it.id }
    studios?.data?.mapTo(ids) { it.id }
    users?.data?.mapTo(ids) { it.id }
    return ids
}
