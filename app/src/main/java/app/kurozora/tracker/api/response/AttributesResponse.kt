package app.kurozora.tracker.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AttributesResponse(
    /**
     * The slug of the resource.
      */
    @SerializedName("slug")
    val slug: String,

    /**
     * The title of the explore category.
     */
    @SerializedName("title")
    val title: String,

    /**
     * The description of the explore category.
     */
    @SerializedName("description")
    val description: String?,

    /**
     * The position of the explore category.
     */
    @SerializedName("position")
    val position: Int,

    /**
     * The type of the explore category.
     */
    @SerializedName("type")
    val type: String,

    /**
     * The size of the explore category.
     */
    @SerializedName("size")
    val size: String,
): Serializable
