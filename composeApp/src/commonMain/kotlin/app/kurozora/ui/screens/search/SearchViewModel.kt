package app.kurozora.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.components.cards.MediaCardViewMode
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import kurozorakit.shared.logging.KurozoraLogger

class SearchViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    // 🆕 Suggestions için debounce job
    private var suggestionJob: Job? = null

    // 🆕 Suggestions'ları çek
    fun fetchSuggestions(query: String) {
        KurozoraLogger.debug("[SearchViewModel]", "fetchSuggestions: query=$query")
        suggestionJob?.cancel()

        suggestionJob = viewModelScope.launch {
            // Sadece boş query'de veya çok kısa query'de suggestions göster
            if (query.isNotEmpty() && query.length < 2) {
                _state.update { it.copy(suggestions = emptyList()) }
                return@launch
            }

            delay(300) // Debounce

            val result = kurozoraKit.search().getSearchSuggestions(
                scope = KKSearchScope.kurozora,
                types = listOf(KKSearchType.shows),
                query = query
            )

            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            suggestions = result.data.data.take(10) // Max 10 suggestion
                        )
                    }
                }
                is Result.Error -> {
                    // Suggestions hatasını gösterme, sessizce hata al
                    KurozoraLogger.warning("[SearchViewModel]", "Suggestions error: ${result.error.message}")
                    _state.update { it.copy(suggestions = emptyList()) }
                }
            }
        }
    }

    /** 🔍 Normal arama — context-aware: preserves activeType if set (from browse card) */
    fun search(query: String) {
        val currentActiveType = _state.value.activeType
        _state.update {
            it.copy(
                query = query,
                isLoading = query.isNotEmpty(),
                errorMessage = null,
            )
        }
        if (query.isEmpty()) {
            clearSearch()
            return
        }
        if (currentActiveType != null) {
            performSearch(query, listOf(currentActiveType))
        } else {
            val selectedTypesList = _state.value.selectedTypes.toList()
            if (selectedTypesList.isNotEmpty()) {
                performSearch(query, selectedTypesList)
            } else {
                performSearch(query, allTypes())
            }
        }
    }

    fun searchWithSuggestion(suggestion: String) {
        _state.update {
            it.copy(
                query = suggestion,
                suggestions = emptyList()
            )
        }
        search(suggestion)
    }

    private fun clearSearch() {
        _state.update {
            it.copy(
                characterIds = emptyList(),
                episodeIds = emptyList(),
                gameIds = emptyList(),
                literatureIds = emptyList(),
                peopleIds = emptyList(),
                showIds = emptyList(),
                songIds = emptyList(),
                studioIds = emptyList(),
                userIds = emptyList(),
                isLoading = false,
                activeType = null
            )
        }
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
        if (q.isNotEmpty()) {
            performSearch(q, allTypes())
        }
    }

    fun toggleType(type: KKSearchType) {
        _state.update { current ->
            val newSelected = current.selectedTypes.toMutableSet().apply {
                if (contains(type)) remove(type) else add(type)
            }
            val newState = current.copy(selectedTypes = newSelected)
            newState
        }

        // 🆕 Seçim değiştiğinde mevcut query ile yeniden ara
        val currentQuery = _state.value.query
        if (currentQuery.isNotEmpty()) {
            search(currentQuery)
        }

    }

    /** 🧩 Arama işlemi - seçili typelara göre */
    private fun performSearch(
        query: String,
        types: List<KKSearchType>,
        filter: KKSearchFilter? = null
    ) {
        KurozoraLogger.debug("[SearchViewModel]", "performSearch: query=$query, types=$types")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // 🆕 Eğer filter yoksa state'deki filter'ı kullan
            val activeFilter = filter ?: _state.value.filter

            kurozoraKit.search().search(
                scope = KKSearchScope.kurozora,
                types = types,  // 🆕 Artık seçili typelar geliyor
                query = query,
                filter = activeFilter
            ).onSuccess { res ->
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        characterIds = if (types.contains(KKSearchType.characters)) res.data.characters?.data?.map { it.id } ?: emptyList() else current.characterIds,
                        episodeIds = if (types.contains(KKSearchType.episodes)) res.data.episodes?.data?.map { it.id } ?: emptyList() else current.episodeIds,
                        gameIds = if (types.contains(KKSearchType.games)) res.data.games?.data?.map { it.id } ?: emptyList() else current.gameIds,
                        showIds = if (types.contains(KKSearchType.shows)) res.data.shows?.data?.map { it.id } ?: emptyList() else current.showIds,
                        literatureIds = if (types.contains(KKSearchType.literatures)) res.data.literatures?.data?.map { it.id } ?: emptyList() else current.literatureIds,
                        peopleIds = if (types.contains(KKSearchType.people)) res.data.people?.data?.map { it.id } ?: emptyList() else current.peopleIds,
                        songIds = if (types.contains(KKSearchType.songs)) res.data.songs?.data?.map { it.id } ?: emptyList() else current.songIds,
                        studioIds = if (types.contains(KKSearchType.studios)) res.data.studios?.data?.map { it.id } ?: emptyList() else current.studioIds,
                        userIds = if (types.contains(KKSearchType.users)) res.data.users?.data?.map { it.id } ?: emptyList() else current.userIds,
                        // ----------------------------------------------
                        characterNext = if (types.contains(KKSearchType.characters)) res.data.characters?.next else current.characterNext,
                        episodeNext = if (types.contains(KKSearchType.episodes)) res.data.episodes?.next else current.episodeNext,
                        gameNext = if (types.contains(KKSearchType.games)) res.data.games?.next else current.gameNext,
                        literatureNext = if (types.contains(KKSearchType.literatures)) res.data.literatures?.next else current.literatureNext,
                        showNext = if (types.contains(KKSearchType.shows)) res.data.shows?.next else current.showNext,
                        peopleNext = if (types.contains(KKSearchType.people)) res.data.people?.next else current.peopleNext,
                        songNext = if (types.contains(KKSearchType.songs)) res.data.songs?.next else current.songNext,
                        studioNext = if (types.contains(KKSearchType.studios)) res.data.studios?.next else current.studioNext,
                        userNext = if (types.contains(KKSearchType.users)) res.data.users?.next else current.userNext,
                    )
                }
            }.onError { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun setActiveType(type: KKSearchType) {
        _state.update { it.copy(activeType = type) }
    }

    fun setSelectedTabType(type: KKSearchType) {
        _state.update { it.copy(selectedTabType = type) }
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
        _state.update { it.copy(activeFilter = filter, filter = f) }
    }

    fun applyFilter() {
        val current = _state.value
        val query = current.query
        val type = current.activeType

        val types: List<KKSearchType> = if (type != null) {
            listOf(type)
        } else {
            val filterType = current.filter?.let { f ->
                when (f) {
                    is KKSearchFilter.Show -> KKSearchType.shows
                    is KKSearchFilter.Literature -> KKSearchType.literatures
                    is KKSearchFilter.Character -> KKSearchType.characters
                    is KKSearchFilter.Episode -> KKSearchType.episodes
                    is KKSearchFilter.Game -> KKSearchType.games
                    is KKSearchFilter.Person -> KKSearchType.people
                    is KKSearchFilter.Song -> KKSearchType.songs
                    is KKSearchFilter.Studio -> KKSearchType.studios
                    else -> null
                }
            }
            if (filterType != null) listOfNotNull(filterType) else current.selectedTypes.toList()
        }

        if (types.isNotEmpty()) {
            performSearch(query, types, current.filter)
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
        KurozoraLogger.debug("[SearchViewModel]", "loadMore: type=$type")
        // 🆕 Sadece seçili tip varsa load more yap
        if (!_state.value.selectedTypes.contains(type) && _state.value.activeType != type) {
            return
        }

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
        } ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, errorMessage = null) }

            kurozoraKit.search().search(
                scope = KKSearchScope.kurozora,
                types = listOf(type), // 🆕 Sadece bu tip için yükle
                query = _state.value.query,
                filter = _state.value.filter,
                next = next.removePrefix("/v1/")
            ).onSuccess { res ->
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
                    val newIds = when (type) {
                        KKSearchType.characters -> res.data.characters?.data?.map { it.id } ?: emptyList()
                        KKSearchType.episodes -> res.data.episodes?.data?.map { it.id } ?: emptyList()
                        KKSearchType.games -> res.data.games?.data?.map { it.id } ?: emptyList()
                        KKSearchType.literatures -> res.data.literatures?.data?.map { it.id } ?: emptyList()
                        KKSearchType.shows -> res.data.shows?.data?.map { it.id } ?: emptyList()
                        KKSearchType.people -> res.data.people?.data?.map { it.id } ?: emptyList()
                        KKSearchType.songs -> res.data.songs?.data?.map { it.id } ?: emptyList()
                        KKSearchType.studios -> res.data.studios?.data?.map { it.id } ?: emptyList()
                        KKSearchType.users -> res.data.users?.data?.map { it.id } ?: emptyList()
                    }
                    val nextValue = when (type) {
                        KKSearchType.characters -> res.data.characters?.next
                        KKSearchType.episodes -> res.data.episodes?.next
                        KKSearchType.games -> res.data.games?.next
                        KKSearchType.literatures -> res.data.literatures?.next
                        KKSearchType.shows -> res.data.shows?.next
                        KKSearchType.people -> res.data.people?.next
                        KKSearchType.songs -> res.data.songs?.next
                        KKSearchType.studios -> res.data.studios?.next
                        KKSearchType.users -> res.data.users?.next
                    }

                    when (type) {
                        KKSearchType.characters -> it.copy(characterIds = oldIds + newIds, characterNext = nextValue, isLoadingMore = false)
                        KKSearchType.episodes -> it.copy(episodeIds = oldIds + newIds, episodeNext = nextValue, isLoadingMore = false)
                        KKSearchType.games -> it.copy(gameIds = oldIds + newIds, gameNext = nextValue, isLoadingMore = false)
                        KKSearchType.literatures -> it.copy(literatureIds = oldIds + newIds, literatureNext = nextValue, isLoadingMore = false)
                        KKSearchType.shows -> it.copy(showIds = oldIds + newIds, showNext = nextValue, isLoadingMore = false)
                        KKSearchType.people -> it.copy(peopleIds = oldIds + newIds, peopleNext = nextValue, isLoadingMore = false)
                        KKSearchType.songs -> it.copy(songIds = oldIds + newIds, songNext = nextValue, isLoadingMore = false)
                        KKSearchType.studios -> it.copy(studioIds = oldIds + newIds, studioNext = nextValue, isLoadingMore = false)
                        KKSearchType.users -> it.copy(userIds = oldIds + newIds, userNext = nextValue, isLoadingMore = false)
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
        KurozoraLogger.debug("[SearchViewModel]", "updateLibraryStatus: itemId=$itemId, newStatus=$newStatus, type=$type")
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
                val result = kurozoraKit.user().addToLibrary(kind, newStatus, listOf(itemId))

                if (result !is Result.Success) {
                    KurozoraLogger.warning("[SearchViewModel]", "Failed to update status ($itemId → $newStatus): $result")
                    return@launch
                }

                KurozoraLogger.info("[SearchViewModel]", "Library updated ($itemId → $newStatus)")

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
                KurozoraLogger.error("[SearchViewModel]", "updateLibraryStatus error", e)
            }
        }
    }

    fun markEpisodeAsWatched(episodeId: String) {
        KurozoraLogger.debug("[SearchViewModel]", "markEpisodeAsWatched: episodeId=$episodeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1️⃣ API çağrısı
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)
                if (result !is Result.Success) {
                    KurozoraLogger.warning("[SearchViewModel]", "Failed to mark episode as watched: $result")
                    return@launch
                }
                val watchStatus = result.data.data.watchStatus
                KurozoraLogger.info("[SearchViewModel]", "Episode marked as watched: $episodeId → Watch Status: $watchStatus")
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
                KurozoraLogger.error("[SearchViewModel]", "Error marking episode watched", e)
            }
        }
    }

    fun followUser(userId: String) {
        KurozoraLogger.debug("[SearchViewModel]", "followUser: userId=$userId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.auth().updateFollowStatus(userId)
                if (result !is Result.Success) {
                    KurozoraLogger.warning("[SearchViewModel]", "Failed to update follow status for $userId: $result")
                    return@launch
                }
                val newStatus = result.data.data.followStatus
                KurozoraLogger.info("[SearchViewModel]", "Follow status updated for $userId → $newStatus")
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
                KurozoraLogger.error("[SearchViewModel]", "Error followUser", e)
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
