package app.kurozora.core.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kurozorakit.shared.logging.KurozoraLogger

class AccountScopedSettings(
    private val settingsOrRoot: Settings,
    private val namespace: String? = null,
) {
    companion object KEYS {
        const val THEME_KEY = "theme"
        const val LANGUAGE_KEY = "language"
        const val APP_ICON_KEY = "app_icon"
    }

    operator fun get(key: String): String? = settingsOrRoot.getStringOrNull(key(key))
    operator fun set(key: String, value: String) {
        settingsOrRoot[key(key)] = value
    }

    private fun key(k: String): String =
        namespace?.let { "$it.$k" } ?: k

    var theme: String
        get() = settingsOrRoot.getStringOrNull(key(THEME_KEY)) ?: "default"
        set(value) {
            settingsOrRoot[key(THEME_KEY)] = value
        }
    var language: String
        get() = settingsOrRoot.getStringOrNull(key(LANGUAGE_KEY)) ?: "en"
        set(value) {
            settingsOrRoot[key(LANGUAGE_KEY)] = value
            KurozoraLogger.debug("[AccountScopedSettings]", "Language set to: $value")
        }
    var icon: String
        get() = settingsOrRoot.getStringOrNull(key(APP_ICON_KEY)) ?: "default"
        set(value) {
            settingsOrRoot[key(APP_ICON_KEY)] = value
            KurozoraLogger.debug("[AccountScopedSettings]", "App icon set to: $value")
        }

    fun clear() {
        if (namespace == null) {
            settingsOrRoot.clear()
        } else {
            val prefix = "$namespace."
            val keysToRemove = settingsOrRoot.keys.filter { it.startsWith(prefix) }
            keysToRemove.forEach { settingsOrRoot.remove(it) }
        }
        KurozoraLogger.info("[AccountScopedSettings]", "Settings cleared for namespace: ${namespace ?: "dedicated"}")
    }
}
