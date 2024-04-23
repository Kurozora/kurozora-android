package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.Api
import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.game.Game
import retrofit2.Callback

class GameService {
    private val api = Api.getClient().create(GameInterface::class.java)

    fun getGame(gameId: String, callback: Callback<BackendResponse<Game>>) {
        val call = api.getGame(gameId)
        call.enqueue(callback)
    }
}
