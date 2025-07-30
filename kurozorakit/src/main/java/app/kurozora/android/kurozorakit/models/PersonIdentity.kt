package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class PersonIdentity(
    val id: String,
    val type: String = "people",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is PersonIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
