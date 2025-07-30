package app.kurozora.tracker.api.service

import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.ExploreCategoryResponse
import retrofit2.Call
import retrofit2.http.GET

interface ExploreInterface {

    @GET(EXPLORE_URL)
    fun getExplorePage() : Call<BackendResponse<ExploreCategoryResponse>>


    companion object {
        const val EXPLORE_URL = "explore"
    }
}