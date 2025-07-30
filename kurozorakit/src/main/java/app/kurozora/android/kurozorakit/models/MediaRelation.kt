package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

/// A root object that stores information about a media relation.
@Serializable
data class MediaRelation(
    /// The name of the relation with the parent show.
    val name: String,
    /// The description of the relation with the parent show.
    val description: String,
)
