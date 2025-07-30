package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class SongIdentityResponse(
    val data: List<SongIdentity>,
    val next: String? = null,
)

