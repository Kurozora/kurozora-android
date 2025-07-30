package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class ShowSongResponse(
    val data: List<ShowSong>,
    val next: String? = null,
)

