package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    val data: List<Media>,
)
