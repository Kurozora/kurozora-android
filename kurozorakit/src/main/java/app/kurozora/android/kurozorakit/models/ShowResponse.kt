package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class ShowResponse(
    val data: List<Show>,
    val next: String?,
)
