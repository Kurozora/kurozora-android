package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class StudioIdentity(
    val id: String,
    val type: String = "studios",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is StudioIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
