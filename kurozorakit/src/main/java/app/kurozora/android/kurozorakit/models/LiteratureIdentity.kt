package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class LiteratureIdentity(
    val id: String,
    val type: String = "literatures",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is LiteratureIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
