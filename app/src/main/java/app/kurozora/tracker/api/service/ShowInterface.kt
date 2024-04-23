package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.show.Show
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ShowInterface {
    @GET("$SHOW_URL/{show_id}")
    fun getShow(@Path("show_id") showId: String): Call<BackendResponse<Show>>

    companion object {
        const val SHOW_URL = "anime"
    }
}
