package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class ShowIdentityResponse(
    val data: List<ShowIdentity>,
    val next: String? = null,
)

