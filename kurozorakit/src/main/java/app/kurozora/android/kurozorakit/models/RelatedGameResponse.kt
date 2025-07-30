package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class RelatedGameResponse(
    /// The data included in the response for a related games object request.
    val data: List<RelatedGame>,
    /// The relative URL to the next page in the paginated response.
    val next: String?,
)
