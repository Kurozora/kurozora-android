package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.Api
import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.game.Game
import app.kurozora.tracker.api.response.show.Show
import retrofit2.Callback

class GameService {
    private val api = Api.getClient().create(GameInterface::class.java)

    fun getGame(gameId: Int , callback: Callback<BackendResponse<Game>>) {
        val call = api.getGame(gameId)
        call.enqueue(callback)
    }
}