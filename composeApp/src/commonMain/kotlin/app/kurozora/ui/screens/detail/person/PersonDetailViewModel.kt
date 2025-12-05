package app.kurozora.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.ui.screens.explore.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.character.CharacterIdentityResponse
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.game.GameIdentityResponse
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.literature.LiteratureIdentityResponse
import kurozorakit.data.models.review.Review
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.show.ShowIdentityResponse
import kurozorakit.shared.Result

class PersonDetailViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(PersonDetailState())
    val state: StateFlow<PersonDetailState> = _state.asStateFlow()
    fun fetchPersonDetails(personId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val result = kurozoraKit.people().getPerson(personId)
                if (result !is Result.Success) {
                    _state.update { it.copy(isLoading = false, errorMessage = result.toString()) }
                    return@launch
                }
                val person = result.data.data.firstOrNull() ?: return@launch
                // Paralel istekler
                val (showsRes, literaturesRes, gamesRes, charactersRes) = coroutineScope {
                    val shows = async { kurozoraKit.people().getPersonShows(personId) }
                    val literatures = async { kurozoraKit.people().getPersonLiteratures(personId) }
                    val games = async { kurozoraKit.people().getPersonGames(personId) }
                    val characters = async { kurozoraKit.people().getPersonCharacters(personId) }

                    awaitAll(shows, literatures, games, characters)
                }
                // Başarılı sonuçları ayıkla
                val shows = ((showsRes as? Result.Success)?.data as? ShowIdentityResponse)?.data
                    ?: emptyList()
                val literatures = ((literaturesRes as? Result.Success)?.data as? LiteratureIdentityResponse)?.data
                    ?: emptyList()
                val games = ((gamesRes as? Result.Success)?.data as? GameIdentityResponse)?.data
                    ?: emptyList()
                val characters = ((charactersRes as? Result.Success)?.data as? CharacterIdentityResponse)?.data
                    ?: emptyList()
                val reviews = kurozoraKit.people().getPersonReviews(personId)
                // State güncelle
                _state.update {
                    it.copy(
                        person = person,
                        showIds = shows.map { it.id },
                        literatureIds = literatures.map { it.id },
                        gameIds = games.map { it.id },
                        characterIds = characters.map { it.id },
                        reviews = reviews.getOrNull()?.data ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    /**
     * Lazy load: Show
     */
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

    /**
     * Lazy load: Literature
     */
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

    /**
     * Lazy load: Character
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
     * Lazy load: Game
     */
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
                        SectionType.RelatedShows -> {
                            val updatedShows = _state.value.shows.toMutableMap()
                            val show = updatedShows[itemId]
                            val updated = show?.copy(
                                attributes = show.attributes.copy(
                                    library = show.attributes.library?.copy(status = newStatus)
                                )
                            ) ?: show!!
                            updatedShows[itemId] = updated
                            _state.value = _state.value.copy(shows = updatedShows)
                        }

                        SectionType.RelatedLiteratures -> {
                            val updatedLiteratures = _state.value.literatures.toMutableMap()
                            val lit = updatedLiteratures[itemId]
                            val updated = lit?.copy(
                                attributes = lit.attributes.copy(
                                    library = lit.attributes.library?.copy(status = newStatus)
                                )
                            ) ?: lit!!
                            updatedLiteratures[itemId] = updated
                            _state.value = _state.value.copy(literatures = updatedLiteratures)
                        }

                        SectionType.RelatedGames -> {
                            val updatedGames = _state.value.games.toMutableMap()
                            val game = updatedGames[itemId]
                            val updated = game?.copy(
                                attributes = game.attributes.copy(
                                    library = game.attributes.library?.copy(status = newStatus)
                                )
                            ) ?: game!!
                            updatedGames[itemId] = updated
                            _state.value = _state.value.copy(games = updatedGames)
                        }
                        else -> {}
                    }
                } else {
                    println("⚠️ Failed to update library status for $itemId: $result")
                }
            } catch (e: Exception) {
                println("❌ Error updating library status: ${e.localizedMessage}")
            }
        }
    }
}
