package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// A root object that stores information about a related literature resource.
@Serializable
data class RelatedLiterature @OptIn(ExperimentalUuidApi::class) constructor(
    // The id of the related literature.
    val id: Uuid = Uuid.random(),
    // The literature related to the parent literature.
    val literature: Literature,
    // The attributes belonging to the related literature.
    var attributes: Attributes,
) {
    // MARK: - Functions
    @OptIn(ExperimentalUuidApi::class)
    override fun equals(other: Any?): Boolean {
        return (other is RelatedLiterature) && this.id == other.id
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun hashCode(): Int {
        return id.hashCode()
    }

    // MARK: - Attributes
    @Serializable
    data class Attributes(
        /// The relation between the literature.
        val relation: MediaRelation,
    )
}
