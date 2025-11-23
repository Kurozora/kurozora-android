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
    }

    /**
     * Initial or refresh load
     */
    fun loadInitial(
        fetcher: suspend (next: String?, limit: Int) -> Pair<List<String>, String?>,
        limit: Int = 20,
    ) {
        viewModelScope.launch {
            try {
                val (data, next) = fetcher(null, limit)
                println("loadInitial FETCHER" + "Data: $data" + "Next: $next")
                _state.value = _state.value.copy(
                    itemIds = data,
                    next = next,
                    isLoading = false
                )
            } catch (e: Exception) {
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
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    error = e.localizedMessage ?: "Load more failed"
                )
            }
        }
    }

    fun fetchItemDetail(itemId: String, type: ItemType) {
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
                val result = kurozoraKit.user().addToLibrary(kind, newStatus, itemId)

                if (result !is Result.Success) {
                    println("⚠️ Failed to update status ($itemId → $newStatus): $result")
                    return@launch
                }

                println("✅ Library updated ($itemId → $newStatus)")
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
                println("❌ updateLibraryStatus error: ${e.localizedMessage}")
            }
        }
    }

    fun markEpisodeAsWatched(episodeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) API çağrısı
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)
                if (result !is Result.Success) {
                    println("⚠️ Failed to mark episode as watched: $result")
                    return@launch
                }
                val watchStatus = result.data.data.watchStatus

                println("✅ Episode marked as watched: $episodeId" + "Watch Status: $watchStatus")
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
                println("❌ Error followUser: ${e.localizedMessage}")
            }
        }
    }
}
