package app.kurozora.tracker.api.response.show

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShowAttributes(
    @SerializedName("originalTitle")
    val title : String,

    @SerializedName("tagline")
    val tagLine : String?,

    @SerializedName("poster")
    val poster : Poster,

    @SerializedName("banner")
    val banner : Poster?
) : Serializable
