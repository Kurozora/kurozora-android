package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExploreCategoryResponse(
    @SerializedName("id")
    var id: String,

    @SerializedName("uuid")
    var uuid: String,

    @SerializedName("type")
    var type: String,

    @SerializedName("href")
    var href: String,

    /**
     * The attributes belonging to the explore category.
     */
    @SerializedName("attributes")
    var attributes: AttributesResponse,

    /**
     * The relationships belonging to the explore category.
     */
    @SerializedName("relationships")
    val relationships: RelationshipsResponse,
): Serializable
