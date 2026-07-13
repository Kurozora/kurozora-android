package app.kurozora.ui.theme

import kotlinx.serialization.Serializable

@Serializable
data class CustomTheme(
    val id: String,
    val name: String,
    val version: String,
    val globalTint: String,
    val globalBackground: String,
    val tableViewBackground: String,
    val borderColor: String,
    val tintedBackground: String,
    val tintedButtonText: String,
    val textColor: String,
    val subTextColor: String,
)
