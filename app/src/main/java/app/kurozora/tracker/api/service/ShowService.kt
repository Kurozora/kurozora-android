package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.Api
import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.show.Show
import retrofit2.Callback

class ShowService {
    private val api = Api.getClient().create(ShowInterface::class.java)

    fun getShow(showId: String, callback: Callback<BackendResponse<Show>>) {
        val call = api.getShow(showId)
        call.enqueue(callback)
    }
}
