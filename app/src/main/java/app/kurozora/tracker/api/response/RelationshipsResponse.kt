package app.kurozora.tracker.api.response

import app.kurozora.tracker.api.response.game.GameResponse
import app.kurozora.tracker.api.response.show.ShowResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RelationshipsResponse(
    /**
     * The shows belonging to the explore category.
     */
    @SerializedName("shows")
    var shows: ShowListResponse<ShowResponse>?,

    /**
     * The games belonging to the explore category.
     */
    @SerializedName("games")
    var games: ShowListResponse<GameResponse>?
): Serializable
