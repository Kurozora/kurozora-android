package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class SeasonIdentityResponse(
    val data: List<SeasonIdentity>,
    val next: String? = null,
)

