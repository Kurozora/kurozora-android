package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class LiteratureIdentityResponse(
    val data: List<LiteratureIdentity>,
    val next: String? = null,
)

