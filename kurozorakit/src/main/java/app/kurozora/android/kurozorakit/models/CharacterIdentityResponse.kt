package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class CharacterIdentityResponse(
    val data: List<CharacterIdentity>,
    val next: String? = null,
)

