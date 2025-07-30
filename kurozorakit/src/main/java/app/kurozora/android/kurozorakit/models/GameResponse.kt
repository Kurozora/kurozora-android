package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

/// A root object that stores information about a collection of games.
@Serializable
data class GameResponse(
    /// The data included in the response for a game object request.
    val data: List<Game>,
    /// The relative URL to the next page in the paginated response.
    val next: String?,
)
