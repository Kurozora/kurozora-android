package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class StaffIdentity(
    val id: String,
    val type: String = "staff",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is StaffIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
