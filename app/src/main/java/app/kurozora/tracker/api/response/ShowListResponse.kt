package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowListResponse<T>(
    /**
     * The data included in the response for a show identity object request.
     */
    @SerializedName("data")
    val data: List<T>
): Serializable
