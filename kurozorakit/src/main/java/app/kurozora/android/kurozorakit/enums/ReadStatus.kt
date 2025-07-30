package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ReadStatus(val value: Int) {
    unread(0),
    read(1);

    companion object {
        fun fromBoolean(bool: Boolean): ReadStatus {
            return if (bool) {
                read
            } else {
                unread
            }
        }
    }
}
