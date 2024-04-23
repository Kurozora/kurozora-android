package app.kurozora.tracker.api.response.game

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * A root object that stores information about a game resource.
 */
data class Game(
    @SerializedName("id")
    val id :String,

    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("type")
    val type : String,

    @SerializedName("href")
    val href : String,

    @SerializedName("slug")
    var slug : String?,

    /**
     * The attributes belonging to the game.
     */
    @SerializedName("attributes")
    var attributes : GameAttributes
): Serializable
