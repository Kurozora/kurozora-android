package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class HiddenStatus(val value: Int) {
    notHidden(-1),
    disabled(0),
    hidden(1);

    companion object {
        fun fromBoolean(bool: Boolean?): HiddenStatus {
            return when (bool) {
                true -> hidden
                false -> notHidden
                null -> disabled
            }
        }
    }
}
