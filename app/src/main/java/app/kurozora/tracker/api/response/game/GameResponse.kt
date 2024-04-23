package app.kurozora.tracker.api.response.game

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("href")
    val href: String
): Serializable
