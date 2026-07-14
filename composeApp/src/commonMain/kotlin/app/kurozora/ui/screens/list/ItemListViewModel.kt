package app.kurozora.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.shared.Result
import kurozorakit.shared.logging.KurozoraLogger

class ItemListViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(ItemListState())
    val state: StateFlow<ItemListState> = _state
    fun setPreloadedItems(items: Map<String, Any>) {
        _state.value = _state.value.copy(
            itemIds = items.keys.toList(),
            items = items,
            isLoading = false
        )
        KurozoraLogger.debug("[ItemListViewModel]", "Preloaded items: $items")
    }

    /**
     * Initial or refresh load
     */
    fun loadInitial(
        fetcher: suspend (next: String?, limit: Int) -> Pair<List<String>, String?>,
        limit: Int = 20,
    ) {
        KurozoraLogger.debug("[ItemListViewModel]", "loadInitial: limit=$limit")
        viewModelScope.launch {
            try {
                val (data, next) = fetcher(null, limit)
                KurozoraLogger.debug("[ItemListViewModel]", "loadInitial: data=$data, next=$next")
                _state.value = _state.value.copy(
                    itemIds = data,
                    next = next,
                    isLoading = false
                )
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "Error in loadInitial", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Load more using next URL
     */
    fun loadMore(
        fetcher: suspend (next: String?, limit: Int) -> Pair<List<String>, String?>,
        limit: Int = 20,
    ) {
        KurozoraLogger.debug("[ItemListViewModel]", "loadMore: limit=$limit")
        val nextUrl = _state.value.next?.removePrefix("/v1/") ?: return
        viewModelScope.launch {
            try {
                val (data, newNext) = fetcher(nextUrl, limit)
                _state.value = _state.value.copy(
                    itemIds = _state.value.itemIds + data,
                    next = newNext,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "Error in loadMore", e)
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    error = e.localizedMessage ?: "Load more failed"
                )
            }
        }
    }

    fun fetchItemDetail(itemId: String, type: ItemType) {
        KurozoraLogger.debug("[ItemListViewModel]", "fetchItemDetail: itemId=$itemId, type=$type")
        if (_state.value.loadingItems.contains(itemId)) return

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                loadingItems = _state.value.loadingItems + itemId
            )
            val item: Any? = when (type) {
                ItemType.Show -> (kurozoraKit.show()
                    .getShow(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Game -> (kurozoraKit.game()
                    .getGame(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Literature -> (kurozoraKit.literature()
                    .getLiterature(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Character -> (kurozoraKit.character()
                    .getCharacter(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Episode -> (kurozoraKit.episode()
                    .getEpisode(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Genre -> (kurozoraKit.genre()
                    .getGenre(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Theme -> (kurozoraKit.theme()
                    .getTheme(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Song -> (kurozoraKit.song()
                    .getSong(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Person -> (kurozoraKit.people()
                    .getPerson(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Season -> (kurozoraKit.season()
                    .getDetails(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Studio -> (kurozoraKit.studio()
                    .getStudio(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.Cast -> (kurozoraKit.cast()
                    .getCast(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                ItemType.User -> (kurozoraKit.auth()
                    .getUserProfile(itemId) as? Result.Success)?.data?.data?.firstOrNull()

                else -> null
            }

            if (item != null) {
                val updated = _state.value.items.toMutableMap()
                updated[itemId] = item

                _state.value = _state.value.copy(
                    items = updated,
                    loadingItems = _state.value.loadingItems - itemId
                )
            } else {
                _state.value = _state.value.copy(
                    loadingItems = _state.value.loadingItems - itemId
                )
            }
        }
    }

    fun updateLibraryStatus(
        itemId: String,
        newStatus: KKLibrary.Status,
        type: ItemType,
    ) {
        KurozoraLogger.debug("[ItemListViewModel]", "updateLibraryStatus: itemId=$itemId, newStatus=$newStatus, type=$type")
        viewModelScope.launch(Dispatchers.IO) {
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
                    KurozoraLogger.warning("[ItemListViewModel]", "Failed to update status ($itemId → $newStatus): $result")
                    return@launch
                }

                KurozoraLogger.info("[ItemListViewModel]", "Library updated ($itemId → $newStatus)")
                // 3) Mevcut item'ı al
                val current = _state.value.items[itemId] ?: return@launch
                // 4) TYPE’e göre library içindeki status'u güncelle
                val updatedItem: Any = when (current) {
                    is kurozorakit.data.models.show.Show -> {
                        val updatedLibrary = current.attributes.library?.copy(status = newStatus)
                        current.copy(attributes = current.attributes.copy(library = updatedLibrary))
                    }

                    is kurozorakit.data.models.literature.Literature -> {
                        val updatedLibrary = current.attributes.library?.copy(status = newStatus)
                        current.copy(attributes = current.attributes.copy(library = updatedLibrary))
                    }

                    is kurozorakit.data.models.game.Game -> {
                        val updatedLibrary = current.attributes.library?.copy(status = newStatus)
                        current.copy(attributes = current.attributes.copy(library = updatedLibrary))
                    }

                    else -> current
                }
                // 5) Map’i güncelle
                val newMap = _state.value.items.toMutableMap()
                newMap[itemId] = updatedItem
                // 6) State’e yaz
                _state.update { it.copy(items = newMap) }
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "updateLibraryStatus error", e)
            }
        }
    }

    fun markEpisodeAsWatched(episodeId: String) {
        KurozoraLogger.debug("[ItemListViewModel]", "markEpisodeAsWatched: episodeId=$episodeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) API çağrısı
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)
                if (result !is Result.Success) {
                    KurozoraLogger.warning("[ItemListViewModel]", "Failed to mark episode as watched: $result")
                    return@launch
                }
                val watchStatus = result.data.data.watchStatus

                KurozoraLogger.info("[ItemListViewModel]", "Episode marked as watched: $episodeId, Watch Status: $watchStatus")
                // 2) Eğer item listesinde bu episode varsa güncelle
                val current = _state.value.items[episodeId] ?: return@launch
                // 3) Episode modelinin "watched" flag'ini güncelle
                val updatedEpisode = when (current) {
                    is kurozorakit.data.models.episode.Episode -> {
                        current.copy(
                            attributes = current.attributes.copy(
                                //isWatched = true
                                _watchStatus = watchStatus
                            )
                        )
                    }

                    else -> current
                }
                // 4) State güncelle
                _state.update { state ->
                    state.copy(
                        items = state.items + (episodeId to updatedEpisode)
                    )
                }
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "Error marking episode watched", e)
            }
        }
    }

    fun blockUser(userId: String) {
        KurozoraLogger.debug("[ItemListViewModel]", "blockUser: userId=$userId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.auth().updateBlockStatus(userId)

                if (result !is Result.Success) {
                    KurozoraLogger.warning("[ItemListViewModel]", "Failed to update block status for $userId: $result")
                    return@launch
                }
                val newStatus = result.data.data.blockStatus
                KurozoraLogger.info("[ItemListViewModel]", "Block status updated for $userId → $newStatus")
                val current = _state.value.items[userId] ?: return@launch
                val updatedUser = when (current) {
                    is kurozorakit.data.models.user.User -> {
                        current.copy(
                            attributes = current.attributes.copy(
                                _blockStatus = newStatus
                            )
                        )
                    }
                    else -> current
                }
                _state.update { state ->
                    state.copy(
                        items = state.items + (userId to updatedUser)
                    )
                }
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "Error blockUser", e)
            }
        }
    }

    fun followUser(userId: String) {
        KurozoraLogger.debug("[ItemListViewModel]", "followUser: userId=$userId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.auth().updateFollowStatus(userId)

                if (result !is Result.Success) {
                    KurozoraLogger.warning("[ItemListViewModel]", "Failed to update follow status for $userId: $result")
                    return@launch
                }
                val newStatus = result.data.data.followStatus
                KurozoraLogger.info("[ItemListViewModel]", "Follow status updated for $userId → $newStatus")
                // 1) Eğer bu user zaten itemList'te varsa güncelle
                val current = _state.value.items[userId] ?: return@launch
                val updatedUser = when (current) {
                    is kurozorakit.data.models.user.User -> {
                        current.copy(
                            attributes = current.attributes.copy(
                                _followStatus = newStatus
                            )
                        )
                    }

                    else -> current
                }
                // 3) State güncelle
                _state.update { state ->
                    state.copy(
                        items = state.items + (userId to updatedUser)
                    )
                }
            } catch (e: Exception) {
                KurozoraLogger.error("[ItemListViewModel]", "Error followUser", e)
            }
        }
    }
}
