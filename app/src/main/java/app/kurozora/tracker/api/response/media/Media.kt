package app.kurozora.tracker.api.response.media

import com.google.gson.annotations.SerializedName

/**
 * A root object that stores information about a media resource.
 */
data class Media(
    /**
     * The url of the media.
     */
    @SerializedName("url")
    val url: String,

    /**
     * The height of the media.
     */
    @SerializedName("height")
    val height: Int? = null,

    /**
     * The width of the media.
     */
    @SerializedName("width")
    val width: Int? = null,

    /**
     * The background color of the media.
     */
    @SerializedName("backgruondColor")
    val backgroundColor: String? = null,

    /**
     * The primary text color of the media.
     */
    @SerializedName("textColor1")
    val textColor1: String? = null,

    /**
     * The secondary text color of the media.
     */
    @SerializedName("textColor2")
    val textColor2: String? = null,

    /**
     * The tertiary text color of the media.
     */
    @SerializedName("textColor3")
    val textColor3: String? = null,

    /**
     * The quaternary text color of the media.
     */
    @SerializedName("textColor4")
    val textColor4: String? = null
)
