package app.kurozora.core.theme

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import app.kurozora.ui.theme.CustomTheme

class DownloadedThemeManager(
    private val settings: Settings,
) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val DOWNLOADED_THEMES_KEY = "app.kurozora.downloaded_themes"
    }

    fun getAllThemes(): List<CustomTheme> {
        val raw = settings.getStringOrNull(DOWNLOADED_THEMES_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<CustomTheme>>(raw) }
            .getOrDefault(emptyList())
    }

    fun getTheme(id: String): CustomTheme? {
        return getAllThemes().find { it.id == id }
    }

    fun addTheme(theme: CustomTheme) {
        val themes = getAllThemes().toMutableList()
        val idx = themes.indexOfFirst { it.id == theme.id }
        if (idx >= 0) themes[idx] = theme else themes.add(theme)
        persist(themes)
    }

    fun removeTheme(id: String) {
        val themes = getAllThemes().filterNot { it.id == id }
        persist(themes)
    }

    private fun persist(themes: List<CustomTheme>) {
        settings.putString(DOWNLOADED_THEMES_KEY, json.encodeToString(themes))
    }
}
