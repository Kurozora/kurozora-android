package app.kurozora.core.settings

import app.kurozora.ui.theme.ThemeController
import app.kurozora.ui.theme.ThemeType
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

    private val onSetCallbacks = mapOf(
        THEME_KEY to { value: String ->
            val newTheme = when (value.lowercase().trim()) {
                "default" -> ThemeType.DEFAULT
                "black" -> ThemeType.BLACK
                "day" -> ThemeType.DAY
                "grass" -> ThemeType.GRASS
                "night" -> ThemeType.NIGHT
                "sakura" -> ThemeType.SAKURA
                "sky" -> ThemeType.SKY
                else -> ThemeType.DEFAULT
            }
            ThemeController.setTheme(newTheme)
            KurozoraLogger.debug("[AccountScopedSettings]", "Theme changed to: $newTheme")
        }
    )

    operator fun get(key: String): String? = settingsOrRoot.getStringOrNull(key(key))
    operator fun set(key: String, value: String) {
        settingsOrRoot[key(key)] = value
        onSetCallbacks[key]?.invoke(value)
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
