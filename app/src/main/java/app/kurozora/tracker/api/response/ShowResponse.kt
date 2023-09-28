package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowResponse(
    @SerializedName("id")
    val id :Int,

    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("type")
    val type : String,

    @SerializedName("href")
    val href : String
) : Serializable

