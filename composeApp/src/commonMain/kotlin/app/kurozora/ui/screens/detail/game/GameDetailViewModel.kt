package app.kurozora.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.show.cast.Cast
import kurozorakit.data.models.show.related.RelatedGame
import kurozorakit.data.models.show.related.RelatedLiterature
import kurozorakit.data.models.show.related.RelatedShow
import kurozorakit.data.models.studio.Studio
import kurozorakit.shared.Result
import kotlin.uuid.ExperimentalUuidApi

class GameDetailViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(GameDetailState())
    val state: StateFlow<GameDetailState> = _state.asStateFlow()

    /**
     * Başta show ve ilişkilerdeki ID’leri fetch et
     */
    @OptIn(ExperimentalUuidApi::class)
    fun fetchGameDetails(gameId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.game().getGame(
                gameId, listOf(
                    "cast", "characters", "related-shows", "related-games", "related-literatures", "songs", "staff", "studios"
                )
            )
            val moreByStudioIds = kurozoraKit.game().getMoreByStudio(gameId)
            val reviews = kurozoraKit.game().getGameReviews(gameId)

            if (result is Result.Success) {
                val game = result.data.data.firstOrNull() ?: return@launch
                val relationships = game.relationships

                _state.update { it ->
                    it.copy(
                        game = game,
                        castIds = relationships?.cast?.data?.map { it.id } ?: emptyList(),
                        characterIds = relationships?.characters?.data?.map { it.id }
                            ?: emptyList(),
                        relatedShows = relationships?.relatedShows?.data as List<RelatedShow>,
                        relatedGames = relationships.relatedGames?.data as List<RelatedGame>,
                        relatedLiteratures = relationships.relatedLiteratures?.data as List<RelatedLiterature>,
                        peopleIds = relationships.people?.data?.map { it.id } ?: emptyList(),
                        staffIds = relationships.staff?.data?.map { it.id } ?: emptyList(),
                        studioIds = relationships.studios?.data?.map { it.id } ?: emptyList(),
                        moreByStudioIds = moreByStudioIds.getOrNull()?.data?.map { it.id }
                            .orEmpty(),
                        reviews = reviews.getOrNull()?.data ?: emptyList(),
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
     * Lazy load için character fetch
     */
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

    /**
     * Lazy load için person fetch
     */
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

    fun fetchMoreByStudioGame(id: String) {
        if (_state.value.moreByStudio.containsKey(id)) return

        viewModelScope.launch {
            _state.update { it.copy(loadingItems = _state.value.loadingItems + id) }
            val res = kurozoraKit.game().getGame(id)
            val game: Game? = (res as? Result.Success)?.data?.data?.firstOrNull()

            if (game != null) {
                val updated = _state.value.moreByStudio.toMutableMap()
                updated[id] = game
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
        section: SectionType,
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
                            val updatedGame = _state.value.game?.copy(
                                attributes = _state.value.game!!.attributes.copy(
                                    library = _state.value.game!!.attributes.library?.copy(status = newStatus)
                                )
                            )
                            _state.value = _state.value.copy(game = updatedGame)
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
            val current = state.value.game?.attributes?.library?.isFavorited == true
            // 2) Optimistic UI update
            _state.update {
                it.copy(
                    game = it.game?.copy(
                        attributes = it.game.attributes.copy(
                            library = it.game.attributes.library?.copy(isFavorited = !current)
                        )
                    )
                )
            }
            // 3) API çağrısı
            val result = kurozoraKit
                .user()
                .updateMyFavorites(KKLibrary.Kind.GAMES, modelId)
            // 4) Başarısızsa rollback
            if (result !is Result.Success) {
                println("❌ Favorite update failed: $result")
                // rollback
                _state.update {
                    it.copy(
                        game = it.game?.copy(
                            attributes = it.game.attributes.copy(
                                library = it.game.attributes.library?.copy(isFavorited = current)
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
            val current = state.value.game?.attributes?.library?.isReminded == true
            // 2) Optimistic UI update
            _state.update {
                it.copy(
                    game = it.game?.copy(
                        attributes = it.game.attributes.copy(
                            library = it.game.attributes.library?.copy(
                                isReminded = !current   // toggle
                            )
                        )
                    )
                )
            }
            // 3) API çağrısı
            val result = kurozoraKit
                .user()
                .updateReminderStatus(KKLibrary.Kind.GAMES, modelId)
            // 4) Başarısızsa rollback
            if (result !is Result.Success) {
                println("❌ Reminder status update failed: $result")

                _state.update {
                    it.copy(
                        game = it.game?.copy(
                            attributes = it.game.attributes.copy(
                                library = it.game.attributes.library?.copy(
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

    fun postReview(gameId: String, score: Int, review: String) {
        viewModelScope.launch {
            val result = kurozoraKit.game().rateGame(
                gameId = gameId,
                rating = score.toDouble(),
                review = review
            )
            when (result) {
                is Result.Success -> {
                    val updatedReviews = kurozoraKit.game().getGameReviews(gameId)

                    _state.update { currentState ->
                        currentState.copy(
                            reviews = updatedReviews.getOrNull()?.data ?: _state.value.reviews,
                        )
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }
}
