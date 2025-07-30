package app.kurozora.android.kurozorakit.enums

enum class KKScheduleType(val value: Int) {
    shows(0),
    literatures(1),
    games(2);

    companion object {
        val values = values()
    }
}
