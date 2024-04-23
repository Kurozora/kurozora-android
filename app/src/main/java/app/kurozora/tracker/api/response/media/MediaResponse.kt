package app.kurozora.tracker.api.response.media

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * A root object that stores information about a collection of media.
 */
data class MediaResponse(
    /**
     * The data included in the response for a media object request.
     */
    @SerializedName("data")
    val data: List<Media>
) : Serializable

