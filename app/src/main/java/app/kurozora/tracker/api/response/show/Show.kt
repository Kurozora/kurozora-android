package app.kurozora.tracker.api.response.show

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * A root object that stores information about a show resource.
 */
data class Show(
    @SerializedName("id")
    val id :Int,

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
    var attributes : ShowAttributes
): Serializable
