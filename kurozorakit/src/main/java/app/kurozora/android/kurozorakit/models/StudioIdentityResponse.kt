package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class StudioIdentityResponse(
    val data: List<StudioIdentity>,
    val next: String? = null,
)

