package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class StaffIdentityResponse(
    val data: List<StaffIdentity>,
    val next: String? = null,
)

