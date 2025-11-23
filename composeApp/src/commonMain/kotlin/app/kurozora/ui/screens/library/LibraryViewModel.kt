package app.kurozora.ui.screens.library

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
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.show.Show
import kurozorakit.shared.Result

class LibraryViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    init {
        fetchLibrary()
    }

    fun selectTab(tab: LibraryTab) {
        _state.update { it.copy(selectedTab = tab, selectedStatus = KKLibrary.Status.INPROGRESS, items = emptyList(), errorMessage = null) }
        fetchLibrary()
    }

    fun selectStatus(status: KKLibrary.Status) {
        _state.update { it.copy(selectedStatus = status, items = emptyList(), errorMessage = null) }
        fetchLibrary()
    }

    fun fetchLibrary(sortType: KKLibrary.SortType = KKLibrary.SortType.NONE, sortOption: KKLibrary.Option = KKLibrary.Option.NONE) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _state.value
            val result = kurozoraKit.user().getMyLibrary(
                libraryKind = currentState.selectedTab.kind,
                libraryStatus = currentState.selectedStatus,
                sortType = sortType,
                sortOption = sortOption
            )

            if (result is Result.Success) {
                val library = result.data.data // LibraryResponse.data -> Library
                val list = when (currentState.selectedTab) {
                    LibraryTab.Animes -> library.shows ?: emptyList()
                    LibraryTab.Mangas -> library.literatures ?: emptyList()
                    LibraryTab.Games -> library.games ?: emptyList()
                }

                _state.update {
                    it.copy(
                        items = list,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        items = emptyList(),
                        isLoading = false,
                        errorMessage = result.toString()
                    )
                }
            }
        }
    }

    fun updateCardViewMode(mode: MediaCardViewMode) {
        _state.value = _state.value.copy(mediaCard = mode)
    }

    fun updateColumnCount(count: Int) {
        _state.value = _state.value.copy(columnCount = count)
    }

    fun search(query: String) {
        _state.update { it.copy(query = query) }
        performSearch(query, listOf(_state.value.selectedTab.toSearchType()))
    }

    private fun performSearch(query: String, types: List<KKSearchType>, filter: KKSearchFilter? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            kurozoraKit.search().search(
                scope = KKSearchScope.library,
                types = types,
                query = query,
                filter = filter,
            ).onSuccess { res ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        gameIds = res.data.games?.data?.map { it.id } ?: emptyList(),
                        showIds = res.data.shows?.data?.map { it.id } ?: emptyList(),
                        literatureIds = res.data.literatures?.data?.map { it.id } ?: emptyList(),
                        // ----------------------------------------------
                        gameNext = res.data.games?.next,
                        literatureNext = res.data.literatures?.next,
                        showNext = res.data.shows?.next,
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
            is GameFilter -> KKSearchFilter.Game(filter)
            else -> KKSearchFilter.Show(filter as ShowFilter)
        }
        _state.update { it.copy(filter = f) }
    }

    fun applyFilter() {
        val current = _state.value
        val query = current.query
        val type = current.selectedTab.toSearchType()
        performSearch(query, listOf(type), current.filter)
    }

    fun applySort(sortType: KKLibrary.SortType, sortOption: KKLibrary.Option) {
        _state.update { it.copy(sortType = sortType, sortOption = sortOption) }
        fetchLibrary(sortType, sortOption)
    }

    private fun allTypes() = listOf(
        KKSearchType.shows,
        KKSearchType.literatures,
        KKSearchType.games,
    )

    fun updateLibraryStatus(itemId: String, newStatus: KKLibrary.Status, type: ItemType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val kind = when (type) {
                    ItemType.Show -> KKLibrary.Kind.SHOWS
                    ItemType.Literature -> KKLibrary.Kind.LITERATURES
                    ItemType.Game -> KKLibrary.Kind.GAMES
                    else -> null
                }

                if (kind == null) {
                    println("⚠️ Unsupported type for library update: $type")
                    return@launch
                }
                val result = kurozoraKit.user().addToLibrary(kind, newStatus, itemId)

                if (result is Result.Success) {
                    println("✅ Library status updated for $type ($itemId) → $newStatus")
                    // Güncel listeyi al ve öğeyi güncelle
                    val updatedList = _state.value.items.map { item ->
                        when (item) {
                            is Show ->
                                if (item.id == itemId) item.copy(
                                    attributes = item.attributes.copy(
                                        library = item.attributes.library?.copy(status = newStatus)
                                    )
                                ) else item

                            is Literature ->
                                if (item.id == itemId) item.copy(
                                    attributes = item.attributes.copy(
                                        library = item.attributes.library?.copy(status = newStatus)
                                    )
                                ) else item

                            is Game ->
                                if (item.id == itemId) item.copy(
                                    attributes = item.attributes.copy(
                                        library = item.attributes.library?.copy(status = newStatus)
                                    )
                                ) else item

                            else -> item
                        }
                    }

                    _state.value = _state.value.copy(items = updatedList)
                } else {
                    println("⚠️ Failed to update library status for $itemId: $result")
                }
            } catch (e: Exception) {
                println("❌ Error updating library status: ${e.localizedMessage}")
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
}
