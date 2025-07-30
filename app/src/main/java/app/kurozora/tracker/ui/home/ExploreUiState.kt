package app.kurozora.tracker.ui.home

import app.kurozora.tracker.api.response.game.Game
import app.kurozora.tracker.api.response.show.Show
import app.kurozora.tracker.ui.components.BaseUiState

data class ExploreUiState(
    val featuredState : ShowLoadingTaskState = ShowLoadingTaskState.Loading,
    val gamesState : GamesLoadingTaskState = GamesLoadingTaskState.Loading,
    val summerState : ShowLoadingTaskState = ShowLoadingTaskState.Loading,
) : BaseUiState()

sealed class ShowLoadingTaskState : BaseUiState() {
    object Loading : ShowLoadingTaskState()
    data class Loaded(
        val result: List<Show>
    ) :  ShowLoadingTaskState()

    data class Error(
        val error: String
    ) : ShowLoadingTaskState()

    object Empty : ShowLoadingTaskState()
}
sealed class GamesLoadingTaskState : BaseUiState() {
    object Loading : GamesLoadingTaskState()
    data class Loaded(
        val result: List<Game>
    ) : GamesLoadingTaskState()

    data class Error(
        val error: String
    ) : GamesLoadingTaskState()

    object Empty : GamesLoadingTaskState()
}