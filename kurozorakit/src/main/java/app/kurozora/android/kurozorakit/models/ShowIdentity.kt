package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class ShowIdentity(
    val id: String,
    val type: String = "shows",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is ShowIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
