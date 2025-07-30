package app.kurozora.tracker.api.response.show

import com.google.gson.annotations.SerializedName

data class Poster(
    @SerializedName("url")
    val url : String
)
