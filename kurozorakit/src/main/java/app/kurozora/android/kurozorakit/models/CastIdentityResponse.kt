package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class CastIdentityResponse(
    val data: List<CastIdentity>,
    val next: String? = null,
)

