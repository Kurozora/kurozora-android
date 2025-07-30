package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BackendResponse<T>(
    @SerializedName("data")
    var data : List<T>
) : Serializable
