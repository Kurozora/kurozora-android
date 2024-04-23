package app.kurozora.tracker.api.response.show

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("href")
    val href: String
): Serializable

