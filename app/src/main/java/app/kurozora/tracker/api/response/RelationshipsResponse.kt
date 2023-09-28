package app.kurozora.tracker.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RelationshipsResponse(
    @SerializedName("shows")
    var shows : ShowListResponse<ShowResponse>?,

 /*  @SerializedName("games")
   @Expose
    var games : ShowListResponse<GameResponse>?*/
):Serializable
