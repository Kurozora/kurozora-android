package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderStatus(val value: Int) {
    notReminded(-1),
    disabled(0),
    reminded(1);

    companion object {
        fun fromBoolean(bool: Boolean?): ReminderStatus {
            return when (bool) {
                true -> reminded
                false -> notReminded
                null -> disabled
            }
        }
    }
}
