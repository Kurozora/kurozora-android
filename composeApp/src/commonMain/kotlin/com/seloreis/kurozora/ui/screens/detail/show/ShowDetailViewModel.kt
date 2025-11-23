package com.seloreis.kurozora.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seloreis.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.cast.Cast
import kurozorakit.data.models.show.related.RelatedGame
import kurozorakit.data.models.show.related.RelatedLiterature
import kurozorakit.data.models.show.related.RelatedShow
import kurozorakit.data.models.show.song.ShowSong
import kurozorakit.data.models.staff.Staff
import kurozorakit.data.models.studio.Studio
import kurozorakit.shared.Result
import kotlin.uuid.ExperimentalUuidApi

enum class SectionType {
    MainShow,
    RelatedShows,
    RelatedLiteratures,
    RelatedGames,
    MoreByStudio
}


class ShowDetailViewModel(
    private val kurozoraKit: KurozoraKit
) : ViewModel() {

    private val _state = MutableStateFlow(ShowDetailState())
    val state: StateFlow<ShowDetailState> = _state.asStateFlow()

    /**
     * Başta show ve ilişkilerdeki ID’leri fetch et
     */
    @OptIn(ExperimentalUuidApi::class)
    fun fetchShowDetails(showId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = kurozoraKit.show().getShow(showId, listOf(
                "cast", "characters", "related-shows", "related-games", "related-literatures", "seasons", "songs", "staff", "studios"
            ))

            val moreByStudioIds = kurozoraKit.show().getMoreByStudio(showId)

            if (result is Result.Success) {
                val show = result.data.data.firstOrNull() ?: return@launch
                val relationships = show.relationships

                _state.update { it ->
                    it.copy(
                        show = show,
                        castIds = relationships?.cast?.data?.map { it.id } ?: emptyList(),
                        characters = relationships?.characters?.data as List<Character>,
                        relatedShows = relationships?.relatedShows?.data as List<RelatedShow>,
                        relatedGames = relationships?.relatedGames?.data as List<RelatedGame>,
                        relatedLiteratures = relationships?.relatedLiteratures?.data as List<RelatedLiterature>,
                        seasonIds = relationships?.seasons?.data?.map { it.id } ?: emptyList(),
                        showSongs = relationships?.showSongs?.data as List<ShowSong>,
                        staff = relationships?.staff?.data as List<Staff>,
                        studioIds = relationships?.studios?.data?.map { it.id } ?: emptyList(),
                        moreByStudioIds = moreByStudioIds.getOrNull()?.data?.map { it.id }.orEmpty(),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.toString()) }
            }
        }
    }

    /**
     * Lazy load için cast fetch
     */
    fun fetchCast(id: String) {
        if (_state.value.cast.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }

            val res = kurozoraKit.cast().getCast(id)
            val cast: Cast? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (cast != null) {
                val updated = _state.value.cast.toMutableMap()
                updated[id] = cast
                _state.update {
                    it.copy(cast = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    /**
     * Lazy load için season fetch
     */
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

    fun fetchMoreByStudioShow(id: String) {
        if (_state.value.moreByStudio.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }

            val res = kurozoraKit.show().getShow(id)
            val show: Show? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (show != null) {
                val updated = _state.value.moreByStudio.toMutableMap()
                updated[id] = show
                _state.update {
                    it.copy(moreByStudio = updated, loadingItems = it.loadingItems - id)
                }
            } else {
                _state.update { it.copy(loadingItems = it.loadingItems - id) }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun updateLibraryStatus(
        itemId: String,
        newStatus: KKLibrary.Status,
        type: ItemType,
        section: SectionType
    ) {
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

                    when (section) {
                        SectionType.MainShow -> {
                            val updatedShow = _state.value.show?.copy(
                                attributes = _state.value.show!!.attributes.copy(
                                    library = _state.value.show!!.attributes.library?.copy(status = newStatus)
                                )
                            )
                            _state.value = _state.value.copy(show = updatedShow)
                        }

                        SectionType.RelatedShows -> {
                            val updated = _state.value.relatedShows.map {
                                if (it.show.id == itemId) it.copy(
                                    show = it.show.copy(
                                        attributes = it.show.attributes.copy(
                                            library = it.show.attributes.library?.copy(status = newStatus)
                                        )
                                    )
                                ) else it
                            }
                            _state.value = _state.value.copy(relatedShows = updated)
                        }

                        SectionType.RelatedLiteratures -> {
                            val updated = _state.value.relatedLiteratures.map {
                                if (it.literature.id == itemId) it.copy(
                                    literature = it.literature.copy(
                                        attributes = it.literature.attributes.copy(
                                            library = it.literature.attributes.library?.copy(status = newStatus)
                                        )
                                    )
                                ) else it
                            }
                            _state.value = _state.value.copy(relatedLiteratures = updated)
                        }

                        SectionType.RelatedGames -> {
                            val updated = _state.value.relatedGames.map {
                                if (it.game.id == itemId) it.copy(
                                    game = it.game.copy(
                                        attributes = it.game.attributes.copy(
                                            library = it.game.attributes.library?.copy(status = newStatus)
                                        )
                                    )
                                ) else it
                            }
                            _state.value = _state.value.copy(relatedGames = updated)
                        }

                        SectionType.MoreByStudio -> {
                            val updatedMap = _state.value.moreByStudio.mapValues { (id, show) ->
                                if (id == itemId) show.copy(
                                    attributes = show.attributes.copy(
                                        library = show.attributes.library?.copy(status = newStatus)
                                    )
                                ) else show
                            }
                            _state.value = _state.value.copy(moreByStudio = updatedMap)
                        }
                    }

                } else {
                    println("⚠️ Failed to update library status for $itemId: $result")
                }

            } catch (e: Exception) {
                println("❌ Error updating library status: ${e.localizedMessage}")
            }
        }
    }

    fun updateFavoriteStatus(modelId: String) {
        viewModelScope.launch {
            // 1) Mevcut state
            val current = state.value.show?.attributes?.library?.isFavorited == true

            // 2) Optimistic UI update
            _state.update {
                it.copy(
                    show = it.show?.copy(
                        attributes = it.show.attributes.copy(
                            library = it.show.attributes.library?.copy(isFavorited = !current)
                        )
                    )
                )
            }

            // 3) API çağrısı
            val result = kurozoraKit
                .user()
                .updateMyFavorites(KKLibrary.Kind.SHOWS, modelId)

            // 4) Başarısızsa rollback
            if (result !is Result.Success) {
                println("❌ Favorite update failed: $result")

                // rollback
                _state.update {
                    it.copy(
                        show = it.show?.copy(
                            attributes = it.show.attributes.copy(
                                library = it.show.attributes.library?.copy(isFavorited = current)
                            )
                        )
                    )
                }
            } else {
                println("✅ Favorite updated successfully")
            }
        }
    }

    fun updateReminderStatus(modelId: String) {
        viewModelScope.launch {
            // 1) Mevcut reminder durumu
            val current = state.value.show?.attributes?.library?.isReminded == true

            // 2) Optimistic UI update
            _state.update {
                it.copy(
                    show = it.show?.copy(
                        attributes = it.show.attributes.copy(
                            library = it.show.attributes.library?.copy(
                                isReminded = !current   // toggle
                            )
                        )
                    )
                )
            }

            // 3) API çağrısı
            val result = kurozoraKit
                .user()
                .updateReminderStatus(KKLibrary.Kind.SHOWS, modelId)

            // 4) Başarısızsa rollback
            if (result !is Result.Success) {
                println("❌ Reminder status update failed: $result")

                _state.update {
                    it.copy(
                        show = it.show?.copy(
                            attributes = it.show.attributes.copy(
                                library = it.show.attributes.library?.copy(
                                    isReminded = current   // rollback to original
                                )
                            )
                        )
                    )
                }
            } else {
                println("✅ Reminder updated successfully")
            }
        }
    }

}
