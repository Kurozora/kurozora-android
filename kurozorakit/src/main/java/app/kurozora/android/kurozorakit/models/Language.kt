package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val name: String,
    val code: String,
    val iso6393: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Language) return false
        return code == other.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
