package app.kurozora.tracker.api.response.show

import com.google.gson.annotations.SerializedName

data class Show(

    @SerializedName("id")
    val id :Int,

    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("type")
    val type : String,

    @SerializedName("href")
    val href : String,

    @SerializedName("slug")
    var slug : String?,

    @SerializedName("attributes")
    var attributes : ShowAttributes
)
