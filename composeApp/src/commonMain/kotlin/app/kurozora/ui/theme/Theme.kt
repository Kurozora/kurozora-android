package app.kurozora.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    DEFAULT, BLACK, DAY, GRASS, NIGHT, SAKURA, SKY
}

sealed class ThemeConfig {
    data class BuiltIn(val type: ThemeType) : ThemeConfig()
    data class Custom(val theme: CustomTheme) : ThemeConfig()
}

fun parseColor(hex: String): ULong {
    return hex
        .removePrefix("#")
        .chunked(2)
        .map { it.toInt(16) }
        .let {
            val (r, g, b) = it.take(3)
            val a = if (it.size == 4) it[0] else 255
            ((a shl 24) or (r shl 16) or (g shl 8) or b).toULong()
        }
}

fun Color.Companion.parse(hex: String): Color {
    val clean = hex.removePrefix("#")
    return try {
        when (clean.length) {
            6 -> {
                val colorInt = clean.toLong(16).toInt()
                Color(
                    red = ((colorInt shr 16) and 0xFF) / 255f,
                    green = ((colorInt shr 8) and 0xFF) / 255f,
                    blue = (colorInt and 0xFF) / 255f,
                    alpha = 1f
                )
            }

            8 -> {
                val colorInt = clean.toLong(16).toInt()
                Color(
                    red = ((colorInt shr 16) and 0xFF) / 255f,
                    green = ((colorInt shr 8) and 0xFF) / 255f,
                    blue = (colorInt and 0xFF) / 255f,
                    alpha = ((colorInt shr 24) and 0xFF) / 255f
                )
            }

            else -> Color.Black
        }
    } catch (e: Exception) {
        Color.Black
    }
}

fun themeFromJson(
    globalTint: String,
    globalBackground: String,
    tableViewBackground: String,
    borderColor: String,
    tintedBackground: String,
    tintedButtonText: String,
    textColor: String,
    subTextColor: String,
) = darkColorScheme(
    primary = Color.parse(globalTint),
    onPrimary = Color.parse(tintedButtonText),
    background = Color.parse(globalBackground),
    onBackground = Color.parse(textColor),
    surface = Color.parse(globalBackground),
    onSurface = Color.parse(textColor),
    surfaceVariant = Color.parse(tableViewBackground),
    onSurfaceVariant = Color.parse(subTextColor),
    outline = Color.parse(borderColor),
    tertiaryContainer = Color.parse(tintedBackground),
    onTertiaryContainer = Color.parse(tintedButtonText)
)

private val builtInThemes = mapOf(
    ThemeType.DEFAULT to themeFromJson("#FF9300", "#353A50", "#454F63", "#AFAFAF", "#50577D", "#EEEEEE", "#EEEEEE", "#AFAFAF"),
    ThemeType.BLACK to themeFromJson("#FF9F0A", "#000000", "#0D0D0D", "#696969", "#0C0C0C", "#EEEEEE", "#A5A5A5", "#696969"),
    ThemeType.DAY to themeFromJson("#FF9300", "#FFFFFF", "#F6F6F6", "#9B9B9B", "#FFF3E3", "#FFFFFF", "#000000", "#979797"),
    ThemeType.GRASS to themeFromJson("#FF9300", "#E5EFAC", "#F1F6D5", "#898F67", "#EDFFD8", "#FFFFFF", "#001000", "#898F67"),
    ThemeType.NIGHT to themeFromJson("#FF9300", "#202020", "#2D2D2D", "#A2A2A2", "#46423C", "#EEEEEE", "#EEEEEE", "#A2A2A2"),
    ThemeType.SAKURA to themeFromJson("#FF9300", "#FFDCE3", "#FFEAEE", "#CCB0B5", "#FFC4CE", "#FFFFFF", "#100000", "#A48080"),
    ThemeType.SKY to themeFromJson("#FF9300", "#CAF0FF", "#DFF6FF", "#A1C0CC", "#C4E1FF", "#FFFFFF", "#000010", "#799099")
)

fun colorSchemeFromConfig(config: ThemeConfig): ColorScheme {
    return when (config) {
        is ThemeConfig.BuiltIn -> builtInThemes[config.type] ?: builtInThemes[ThemeType.DEFAULT]!!
        is ThemeConfig.Custom -> {
            val t = config.theme
            themeFromJson(
                globalTint = t.globalTint,
                globalBackground = t.globalBackground,
                tableViewBackground = t.tableViewBackground,
                borderColor = t.borderColor,
                tintedBackground = t.tintedBackground,
                tintedButtonText = t.tintedButtonText,
                textColor = t.textColor,
                subTextColor = t.subTextColor
            )
        }
    }
}

object ThemeController {
    var themeState = mutableStateOf<ThemeConfig>(ThemeConfig.BuiltIn(ThemeType.DEFAULT))
        private set

    fun setTheme(config: ThemeConfig) {
        themeState.value = config
    }

    fun setTheme(t: ThemeType) {
        themeState.value = ThemeConfig.BuiltIn(t)
    }

    fun restoreFromSettings(saved: String, customThemeLookup: (String) -> CustomTheme?) {
        if (saved.startsWith("custom:")) {
            val id = saved.removePrefix("custom:")
            customThemeLookup(id)?.let { setTheme(ThemeConfig.Custom(it)) }
        } else {
            val type = when (saved.lowercase().trim()) {
                "default" -> ThemeType.DEFAULT
                "black" -> ThemeType.BLACK
                "day" -> ThemeType.DAY
                "grass" -> ThemeType.GRASS
                "night" -> ThemeType.NIGHT
                "sakura" -> ThemeType.SAKURA
                "sky" -> ThemeType.SKY
                else -> ThemeType.DEFAULT
            }
            setTheme(type)
        }
    }
}

val LocalThemeConfig = staticCompositionLocalOf<ThemeConfig> { ThemeConfig.BuiltIn(ThemeType.DEFAULT) }

@Composable
fun KurozoraTheme(content: @Composable () -> Unit) {
    val currentConfig = ThemeController.themeState.value
    val colorScheme = colorSchemeFromConfig(currentConfig)

    CompositionLocalProvider(LocalThemeConfig provides currentConfig) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
