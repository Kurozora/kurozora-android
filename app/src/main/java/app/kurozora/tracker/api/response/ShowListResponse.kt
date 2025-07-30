package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowListResponse<T>(
    @SerializedName("data")
    val data : List<T>

) : Serializable
