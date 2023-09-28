package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExploreCategoryResponse(

    @SerializedName("id")
    var id : String,

    @SerializedName("uuid")
    var uuid : String,

    @SerializedName("type")
    var type : String,

    @SerializedName("href")
    var href : String,

    @SerializedName("attributes")
    var attributes : AttributesResponse,

    @SerializedName("relationships")
    val relationships : RelationshipsResponse,


) : Serializable
