package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AttributesResponse(
    @SerializedName("title")
    val title : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("slug")
    val slug : String,

    @SerializedName("secondarySlug")
    val secondarySlug : String,

    @SerializedName("type")
    val type : String,

    @SerializedName("size")
    val size : String,

    @SerializedName("position")
    val position: Int
) : Serializable
