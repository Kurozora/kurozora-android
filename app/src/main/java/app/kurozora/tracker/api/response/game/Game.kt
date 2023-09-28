package app.kurozora.tracker.api.response.game

import app.kurozora.tracker.api.response.show.Poster
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Game(
    @SerializedName("title")
    val title : String,

    @SerializedName("poster")
    val poster: Poster
): Serializable
