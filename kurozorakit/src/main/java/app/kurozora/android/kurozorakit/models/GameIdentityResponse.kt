package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class GameIdentityResponse(
    val data: List<GameIdentity>,
    val next: String? = null,
)

