package app.kurozora.core.settings

import app.kurozora.ui.theme.ThemeController
import app.kurozora.ui.theme.ThemeType
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

/**
 * Eğer namespace == null ise, zaten verilen Settings (factory.create(name)) platform-specific
 * bir instance olacaktır ve doğrudan kullanılır.
 *
 * Eğer namespace != null ise, root Settings üzerinde 'namespace.key' formuyla çalışır.
 */
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
        }
    var icon: String
        get() = settingsOrRoot.getStringOrNull(key(APP_ICON_KEY)) ?: "en"
        set(value) {
            settingsOrRoot[key(APP_ICON_KEY)] = value
        }

    /**
     * Clear only keys under this namespace (fallback). If Settings instance is a dedicated instance
     * (namespace == null and settingsOrRoot was created by factory.create(name)), clear everything in it.
     */
    fun clear() {
        if (namespace == null) {
            settingsOrRoot.clear()
            return
        }
        // Filter keys that start with namespace and remove them.
        val prefix = "$namespace."
        val keysToRemove = settingsOrRoot.keys.filter { it.startsWith(prefix) }
        keysToRemove.forEach { settingsOrRoot.remove(it) }
    }
}
