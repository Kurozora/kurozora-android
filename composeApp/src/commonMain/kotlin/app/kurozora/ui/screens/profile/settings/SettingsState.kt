package app.kurozora.ui.screens.profile.settings

import androidx.compose.runtime.Composable
import app.kurozora.core.settings.AccountScopedSettings

data class SettingsCategory(
    val key: String,
    val title: String,
    val subtitle: String? = null, // yeni
    val icon: @Composable (() -> Unit)? = null, // opsiyonel icon
    val items: List<SettingItem>,
)

//sealed class SettingItem(
//    open val key: String,
//    open val title: String,
//    open val subtitle: String? = null
//) {
//    abstract fun getValue(scopedSettings: AccountScopedSettings): Any
//    abstract fun setValue(scopedSettings: AccountScopedSettings, value: Any)
//
//    data class SwitchSetting(
//        override val key: String,
//        override val title: String,
//        override val subtitle: String? = null,
//        val defaultValue: Boolean = false
//    ) : SettingItem(key, title, subtitle) {
//        override fun getValue(scopedSettings: AccountScopedSettings): Any =
//            scopedSettings[key]?.toBoolean() ?: defaultValue
//
//        override fun setValue(scopedSettings: AccountScopedSettings, value: Any) {
//            scopedSettings[key] = (value as Boolean).toString()
//        }
//    }
//
//    data class SingleSelectSetting(
//        override val key: String,
//        override val title: String,
//        override val subtitle: String? = null,
//        val options: List<String>,
//        val defaultValue: String? = null
//    ) : SettingItem(key, title, subtitle) {
//        override fun getValue(scopedSettings: AccountScopedSettings): Any =
//            scopedSettings[key] ?: defaultValue ?: options.first()
//
//        override fun setValue(scopedSettings: AccountScopedSettings, value: Any) {
//            scopedSettings[key] = value as String
//        }
//    }
//
//    data class MultiSelectSetting(
//        override val key: String,
//        override val title: String,
//        override val subtitle: String? = null,
//        val options: List<String>,
//        val defaultValue: List<String> = emptyList()
//    ) : SettingItem(key, title, subtitle) {
//        override fun getValue(scopedSettings: AccountScopedSettings): Any =
//            scopedSettings[key]?.split(",") ?: defaultValue
//
//        override fun setValue(scopedSettings: AccountScopedSettings, value: Any) {
//            scopedSettings[key] = (value as List<String>).joinToString(",")
//        }
//    }
//}
sealed class SettingItem(
    open val key: String,
    open val title: String,
    open val subtitle: String? = null, // item için alt başlık
) {
    data class SwitchSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val value: Boolean,
    ) : SettingItem(key, title, subtitle)

    data class SingleSelectSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val options: List<String>,
        val selected: String?,
    ) : SettingItem(key, title, subtitle)

    data class MultiSelectSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val options: List<String>,
        val selected: List<String>,
    ) : SettingItem(key, title, subtitle)
}

fun generateSettingsCategories(scopedSettings: AccountScopedSettings): List<SettingsCategory> {
    return listOf(
        SettingsCategory(
            key = "general",
            title = "General",
            subtitle = "Basic app settings",
            items = listOf(
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.THEME_KEY,
                    title = "Theme",
                    options = listOf("default", "black", " day", "grass", "night", "sakura", "sky"),
                    selected = scopedSettings.theme
                ),
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.LANGUAGE_KEY,
                    title = "Language",
                    options = listOf("en", "tr", "jp"),
                    selected = scopedSettings.language
                )
            )
        ),
        SettingsCategory(
            key = "appearance",
            title = "Appearance",
            subtitle = "Appearance for customization",
            items = listOf(
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.APP_ICON_KEY,
                    title = "App Icon",
                    options = listOf("default", "halloween", "kuro-chan"),
                    selected = scopedSettings.icon
                ),
            )
        )
    )
}
