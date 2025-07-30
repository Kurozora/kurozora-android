package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class RelatedLiteratureResponse(
    val data: List<RelatedLiterature>,
    val next: String?,
)
