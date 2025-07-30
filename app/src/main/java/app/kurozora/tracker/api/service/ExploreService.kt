package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.Api
import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.ExploreCategoryResponse
import retrofit2.Callback


class ExploreService {
    private val api = Api.getClient().create(ExploreInterface::class.java)

    fun getExplorePage(callback : Callback<BackendResponse<ExploreCategoryResponse>>){
        val call = api.getExplorePage()
        call.enqueue(callback)
    }
}