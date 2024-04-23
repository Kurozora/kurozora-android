package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.game.Game
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GameInterface {
    @GET("$GAME_URL/{game_id}")
    fun getGame(@Path("game_id") gameId: String): Call<BackendResponse<Game>>

    companion object {
        const val GAME_URL = "games"
    }
}
