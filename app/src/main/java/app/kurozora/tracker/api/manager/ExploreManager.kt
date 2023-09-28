package app.kurozora.tracker.api.manager

import app.kurozora.tracker.api.response.BackendResponse
import app.kurozora.tracker.api.response.ExploreCategoryResponse
import app.kurozora.tracker.api.service.ExploreService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ExploreManager {

    var exploreCategory : List<ExploreCategoryResponse> = listOf()


    fun getExplorePage(onResponse: (List<ExploreCategoryResponse>) -> Unit){
        ExploreService().getExplorePage(object : Callback<BackendResponse<ExploreCategoryResponse>> {
            override fun onResponse(
                call: Call<BackendResponse<ExploreCategoryResponse>>,
                response: Response<BackendResponse<ExploreCategoryResponse>>
            ) {
                if (response.isSuccessful){
                    exploreCategory = response.body()?.data!!
                  onResponse.invoke(exploreCategory)
                }
            }

            override fun onFailure(call: Call<BackendResponse<ExploreCategoryResponse>>, t: Throwable) {
                onResponse.invoke(emptyList())
            }

        })
    }
    fun getCategoryResponses() : List<ExploreCategoryResponse>{
        return  exploreCategory
    }
}