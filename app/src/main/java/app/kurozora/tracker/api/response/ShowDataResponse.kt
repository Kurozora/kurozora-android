package app.kurozora.tracker.api.response

import app.kurozora.tracker.api.response.show.Show
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowDataResponse(

    @SerializedName("data")
    val data : List<Show>
) : Serializable
