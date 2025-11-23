package com.seloreis.kurozora.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.shared.Result
import kurozorakit.data.models.explore.ExploreCategory
import kurozorakit.data.models.genre.Genre
import kurozorakit.data.models.theme.Theme

enum class ItemType {
    Show, Game, Literature, Character, Episode, Genre, Theme, Song, Person, Recap, Studio, Season, Cast, User, Staff
}

class ExploreViewModel(
    private val kurozoraKit: KurozoraKit
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreState())
    val uiState: StateFlow<ExploreState> = _uiState.asStateFlow()

    init {
        fetchExplore()
    }

    /**
     * İlk aşamada kategorileri yükle ve her kategorinin sadece identity listesini sakla
     */
    fun fetchExplore(genreId : String? = null, themeId : String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val exploreResult = kurozoraKit.explore().getExplore(genreId = genreId, themeId = themeId)

                if (exploreResult is Result.Success) {
                    val categories = exploreResult.data.data

                    // Her kategorideki identity'leri çıkar
                    val identityMap = categories.associate { category ->
                        category.id to (category.relationships?.allIdentities() ?: emptyList())
                    }

                    // 🔹 Recap verilerini categoryItems içine ekle
                    val categoryItems = mutableMapOf<String, Map<String, Any>>()

                    categories.forEach { category ->
                        val recapItems = category.relationships?.recaps?.data
                            ?.associateBy { it.id }
                            ?.mapValues { it.value as Any }
                            ?: emptyMap()

                        categoryItems[category.id] = recapItems
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        categories = categories,
                        categoryItemIdentities = identityMap,
                        categoryItems = categoryItems
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Explore data load failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    fun changeGenre(genre: Genre) {
        _uiState.value = _uiState.value.copy(genre = genre)
        fetchExplore(genreId = genre.id)
    }

    fun changeTheme(theme: Theme) {
        _uiState.value = _uiState.value.copy(theme = theme)
        fetchExplore(themeId = theme.id)
    }

//    fun fetchItemDetail(categoryId: String, itemId: String, type: ItemType) {
//        if (type == ItemType.Recap) return // 🔹 Recap için hiçbir şey çekme
//
//        val loadingKey = "${type.name}_$itemId" // 🔹 Benzersiz anahtar oluştur
//
//        if (_uiState.value.loadingItems.contains(loadingKey)) return
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _uiState.value = _uiState.value.copy(
//                loadingItems = _uiState.value.loadingItems + loadingKey
//            )
//
//            val item: Any? = when (type) {
//                ItemType.Show -> (kurozoraKit.show().getShow(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Game -> (kurozoraKit.game().getGame(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Literature -> (kurozoraKit.literature().getLiterature(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Character -> (kurozoraKit.character().getCharacter(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Episode -> (kurozoraKit.episode().getEpisode(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Genre -> (kurozoraKit.genre().getGenre(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Theme -> (kurozoraKit.theme().getTheme(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Song -> (kurozoraKit.song().getSong(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Person -> (kurozoraKit.people().getPerson(itemId) as? Result.Success)?.data?.data?.firstOrNull()
//                ItemType.Recap -> null
//                else -> null
//            }
//
//            if (item != null) {
//                val updatedCategoryItems = _uiState.value.categoryItems.toMutableMap()
//                val currentItems = updatedCategoryItems[categoryId]?.toMutableMap() ?: mutableMapOf()
//                currentItems[itemId] = item
//                updatedCategoryItems[categoryId] = currentItems
//
//                _uiState.value = _uiState.value.copy(
//                    categoryItems = updatedCategoryItems,
//                    loadingItems = _uiState.value.loadingItems - loadingKey
//                )
//            } else {
//                _uiState.value = _uiState.value.copy(
//                    loadingItems = _uiState.value.loadingItems - loadingKey
//                )
//            }
//        }
//    }


    /**
     * Tek bir item için detay fetch et
     */
    fun fetchItemDetail(categoryId: String, itemId: String, type: ItemType) {
        if (type == ItemType.Recap) return // 🔹 Recap için hiçbir şey çekme
        if (_uiState.value.loadingItems.contains(itemId)) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                loadingItems = _uiState.value.loadingItems + itemId
            )

            val item: Any? = when (type) {
                ItemType.Show -> (kurozoraKit.show().getShow(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Game -> (kurozoraKit.game().getGame(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Literature -> (kurozoraKit.literature().getLiterature(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Character -> (kurozoraKit.character().getCharacter(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Episode -> (kurozoraKit.episode().getEpisode(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Genre -> (kurozoraKit.genre().getGenre(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Theme -> (kurozoraKit.theme().getTheme(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Song -> (kurozoraKit.song().getSong(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Person -> (kurozoraKit.people().getPerson(itemId) as? Result.Success)?.data?.data?.firstOrNull()
                ItemType.Recap -> null
                else -> null
            }

            if (item != null) {
                val updatedCategoryItems = _uiState.value.categoryItems.toMutableMap()
                val currentItems = updatedCategoryItems[categoryId]?.toMutableMap() ?: mutableMapOf()
                currentItems[itemId] = item
                updatedCategoryItems[categoryId] = currentItems

                _uiState.value = _uiState.value.copy(
                    categoryItems = updatedCategoryItems,
                    loadingItems = _uiState.value.loadingItems - itemId
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    loadingItems = _uiState.value.loadingItems - itemId
                )
            }
        }
    }


    fun updateLibraryStatus(itemId: String, newStatus: KKLibrary.Status, type: ItemType) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val kind = when (type) {
                    ItemType.Show -> KKLibrary.Kind.SHOWS
                    ItemType.Literature -> KKLibrary.Kind.LITERATURES
                    ItemType.Game -> KKLibrary.Kind.GAMES
                    else -> null
                }

                val result = kind?.let {
                    kurozoraKit.user().addToLibrary(
                        it,
                        newStatus,
                        itemId
                    )
                }

                if (result is Result.Success) {
                    println("✅ Library status updated for $type ($itemId) → $newStatus")

                    val updatedItems = _uiState.value.categoryItems.toMutableMap()
                    updatedItems.forEach { (categoryId, items) ->
                        val mutableItems = items.toMutableMap()
                        val item = mutableItems[itemId]
                        if (item != null) {
                            when (item) {
                                is kurozorakit.data.models.show.Show -> {
                                    val updatedLibrary = item.attributes.library?.copy(status = newStatus)
                                    item.copy(attributes = item.attributes.copy(library = updatedLibrary))
                                    val updated = item.copy(
                                        attributes = item.attributes.copy(
                                            library = updatedLibrary
                                        )
                                    )
                                    mutableItems[itemId] = updated
                                }
                                is kurozorakit.data.models.literature.Literature -> {
                                    val updatedLibrary = item.attributes.library?.copy(status = newStatus)
                                    item.copy(attributes = item.attributes.copy(library = updatedLibrary))
                                    val updated = item.copy(
                                        attributes = item.attributes.copy(
                                            library = updatedLibrary
                                        )
                                    )
                                    mutableItems[itemId] = updated
                                }
                                is kurozorakit.data.models.game.Game -> {
                                    val updatedLibrary = item.attributes.library?.copy(status = newStatus)
                                    item.copy(attributes = item.attributes.copy(library = updatedLibrary))
                                    val updated = item.copy(
                                        attributes = item.attributes.copy(
                                            library = updatedLibrary
                                        )
                                    )
                                    mutableItems[itemId] = updated
                                }
                            }
                            updatedItems[categoryId] = mutableItems
                        }
                    }

                    _uiState.value = _uiState.value.copy(categoryItems = updatedItems)
                } else {
                    println("⚠️ Failed to update library status for $itemId: $result")
                }
            } catch (e: Exception) {
                println("❌ Error updating library status: ${e.localizedMessage}")
            }
        }
    }

    fun markAsWatchedEpisode(episodeId: String, categoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = kurozoraKit.episode().updateEpisodeWatchStatus(episodeId)

                if (result is Result.Success) {
                    println("✅ Episode marked as watched: $episodeId")

                    // 🔹 İlgili kategoriyi yeniden çek
                    val exploreResult = kurozoraKit.explore().getExplore(exploreCategoryId = categoryId)

                    if (exploreResult is Result.Success) {
                        val updatedCategory = exploreResult.data.data.firstOrNull()
                        if (updatedCategory != null) {
                            // 🔹 Yeni relationship’ten episode ID’lerini al
                            val updatedEpisodeIds = updatedCategory.relationships?.episodes?.data
                                ?.map { it.id } ?: emptyList()

                            // 🔹 State güncelle
                            val updatedCategories = _uiState.value.categories.toMutableList()
                            val categoryIndex = updatedCategories.indexOfFirst { it.id == categoryId }
                            if (categoryIndex != -1) {
                                updatedCategories[categoryIndex] = updatedCategory
                            }

                            val updatedIdentities = _uiState.value.categoryItemIdentities.toMutableMap()
                            updatedIdentities[categoryId] = updatedEpisodeIds

                            // 🔹 (isteğe bağlı) mevcut item’ları koru, sadece ilişkileri güncelle
                            val updatedItems = _uiState.value.categoryItems.toMutableMap()

                            _uiState.value = _uiState.value.copy(
                                categories = updatedCategories,
                                categoryItemIdentities = updatedIdentities,
                                categoryItems = updatedItems
                            )

                            println("♻️ Category $categoryId episodes refreshed successfully")
                        } else {
                            println("⚠️ No category data found for $categoryId")
                        }
                    } else {
                        println("⚠️ Failed to refresh category $categoryId: $exploreResult")
                    }
                } else {
                    println("⚠️ Failed to mark episode as watched: $result")
                }
            } catch (e: Exception) {
                println("❌ Error in markAsWatchedEpisode: ${e.localizedMessage}")
            }
        }
    }



}

/**
 * Extension: category relationships içerisindeki tüm identityleri döndür
 */
fun ExploreCategory.Relationships.allIdentities(): List<String> {
    val ids = mutableListOf<String>()
    shows?.data?.mapTo(ids) { it.id }
    games?.data?.mapTo(ids) { it.id }
    episodes?.data?.mapTo(ids) { it.id }
    characters?.data?.mapTo(ids) { it.id }
    literatures?.data?.mapTo(ids) { it.id }
    genres?.data?.mapTo(ids) { it.id }
    themes?.data?.mapTo(ids) { it.id }
    people?.data?.mapTo(ids) { it.id }
    showSongs?.data?.mapTo(ids) { it.id }
    recaps?.data?.mapTo(ids) { it.id }
    return ids
}
