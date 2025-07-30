package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class CastIdentity(
    val id: String,
    val type: String = "cast",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is CastIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
