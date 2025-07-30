package app.kurozora.android.kurozorakit.enums;

import kotlinx.serialization.Serializable;

@Serializable
enum class UserRole(val value: Int) {
    superAdmin(1),
    admin(2),
    editor(3);
}
