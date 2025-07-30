package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaStat(
    val ratingCountList: List<Int>,
    val ratingAverage: Double,
    val ratingCount: Int,
    val rankGlobal: Int,
    val rankTotal: Int,
    val sentiment: String,
    val highestRatingPercentage: Double,
)
