package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class SongResponse(
    /// The data included in the response for a song object request.
    val data: List<Song>,
    /// The relative URL to the next page in the paginated response.
    val next: String?,
)
