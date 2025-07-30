package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class RelatedShowResponse(
    val data: List<RelatedShow>,
    val next: String?,
)
