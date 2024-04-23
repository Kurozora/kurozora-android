package app.kurozora.tracker.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import app.kurozora.tracker.api.manager.ExploreManager
import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.ExploreCategoryResponse
import app.kurozora.tracker.api.response.game.Game
import app.kurozora.tracker.api.response.show.Show
import app.kurozora.tracker.api.service.GameService
import app.kurozora.tracker.api.service.ShowService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class HomeViewModel : ViewModel() {
    var page = mutableStateOf<List<ExploreCategoryResponse>>(listOf())
    private var loadedBestShow = mutableStateOf<List<Show>>(listOf())
    private var loadedBestGame = mutableStateOf<List<Game>>(listOf())
    private val _uiState by lazy { MutableStateFlow(ExploreUiState()) }
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                featuredState = ShowLoadingTaskState.Loading
            )
        }

        ExploreManager.getExplorePage { list ->
            page.value = list
            getAllContent()
        }
    }

    fun getExplorePage() {
        page.value = ExploreManager.getCategoryResponses()
    }

    private fun getAllContent() {
        _uiState.update {
            it.copy(
                featuredState = ShowLoadingTaskState.Loading
            )
        }

        page.value.forEach { exploreCategoryResponse ->
            if (exploreCategoryResponse.attributes.title == "Featured") {
                exploreCategoryResponse.relationships.shows?.data?.forEach { showResponse ->
                    getShow(showResponse.id) { show ->
                        val list = mutableListOf<Show>()
                        list.addAll(loadedBestShow.value)

                        if (show != null) {
                            list.add(show)
                        }

                        loadedBestShow.value = list
                        _uiState.update { exploreState ->
                            exploreState.copy(
                                featuredState = ShowLoadingTaskState.Loaded(
                                    loadedBestShow.value
                                )
                            )
                        }
                    }
                }
            }

            if (exploreCategoryResponse.relationships.games != null) {
                exploreCategoryResponse.relationships.games?.data?.forEach { gameResponse ->
                    getGame(gameResponse.id) { game ->
                        val list = mutableListOf<Game>()
                        list.addAll(loadedBestGame.value)

                        if (game != null) {
                            list.add(game)
                        }

                        loadedBestGame.value = list
                        _uiState.update { exploreState ->
                            exploreState.copy(
                                gamesState = GamesLoadingTaskState.Loaded(
                                    loadedBestGame.value
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getShow(showID: String, onFound: (Show?) -> Unit) {
        var show: Show? = null
        ShowService().getShow(showID, object : Callback<BackendResponse<Show>> {
            override fun onResponse(
                call: Call<BackendResponse<Show>>,
                response: Response<BackendResponse<Show>>
            ) {
                if (response.isSuccessful) {
                    show = response.body()?.data?.first()
                    onFound(show)
                }
            }

            override fun onFailure(call: Call<BackendResponse<Show>>, t: Throwable) {
                onFound(show)
            }
        })
    }

    private fun getGame(gameID: String, onFound: (Game?) -> Unit) {
        var game: Game? = null
        GameService().getGame(gameID, object : Callback<BackendResponse<Game>> {
            override fun onResponse(
                call: Call<BackendResponse<Game>>,
                response: Response<BackendResponse<Game>>
            ) {
                if (response.isSuccessful) {
                    game = response.body()?.data?.first()
                    onFound(game)
                }
            }

            override fun onFailure(call: Call<BackendResponse<Game>>, t: Throwable) {
                onFound(game)
            }
        })
    }
}
