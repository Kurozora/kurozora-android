package app.kurozora.ui.screens.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kurozora.core.settings.AccountManager
import app.kurozora.core.theme.DownloadedThemeManager
import app.kurozora.core.theme.ThemeDownloader
import app.kurozora.ui.components.cards.AppThemeCard
import app.kurozora.ui.components.cards.ThemeCardData
import app.kurozora.ui.theme.ThemeConfig
import app.kurozora.ui.theme.ThemeController
import app.kurozora.ui.theme.ThemeType
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.shared.logging.KurozoraLogger
import org.koin.compose.koinInject

private data class ThemeItem(
    val type: ThemeType,
    val title: String,
    val desc: String,
)

private val builtInItems = listOf(
    ThemeItem(ThemeType.DEFAULT, "Kurozora", "The official Kurozora theme."),
    ThemeItem(ThemeType.BLACK, "Black", "Darkest theme for OLED."),
    ThemeItem(ThemeType.DAY, "Day", "Bright theme for daytime."),
    ThemeItem(ThemeType.GRASS, "Grass", "Fresh green tones."),
    ThemeItem(ThemeType.NIGHT, "Night", "Sleek dark theme."),
    ThemeItem(ThemeType.SAKURA, "Sakura", "Soft pink cherry blossom."),
    ThemeItem(ThemeType.SKY, "Sky", "Light blue sky tones."),
)

@Composable
fun ThemeStoreContent(state: SettingsState) {
    val kurozoraKit: KurozoraKit = koinInject()
    val downloadedThemeManager: DownloadedThemeManager = koinInject()
    val themeDownloader: ThemeDownloader = koinInject()
    val accountManager: AccountManager = koinInject()
    val scope = rememberCoroutineScope()

    var downloadedThemes by remember { mutableStateOf(downloadedThemeManager.getAllThemes()) }
    var storeThemes by remember { mutableStateOf(state.storeThemeItems) }
    val isLoading = state.isLoadingStoreThemes

    val scopedSettings = accountManager.getScopedSettings()
    val activeThemeString = scopedSettings?.theme ?: "default"

    LaunchedEffect(Unit) {
        if (storeThemes.isEmpty() && !isLoading) {
            kurozoraKit.themeStore().getThemeStore().onSuccess { response ->
                storeThemes = response.data
            }.onError { error ->
                KurozoraLogger.error("[ThemeStoreContent]", "Failed to load theme store", error)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    ) {
        Text(
            text = "Built-in Themes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        builtInItems.chunked(2).forEach { chunk ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            ) {
                chunk.forEach { item ->
                    val isActive = activeThemeString == item.type.name.lowercase()
                    AppThemeCard(
                        data = ThemeCardData.BuiltIn(item.type, item.title, item.desc),
                        isActive = isActive,
                        onAction = {
                            scopedSettings?.let { settings ->
                                settings.theme = item.type.name.lowercase()
                            }
                            ThemeController.setTheme(item.type)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (chunk.size < 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        if (downloadedThemes.isNotEmpty()) {
            Text(
                text = "Downloaded Themes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )

            downloadedThemes.chunked(2).forEach { chunk ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                ) {
                    chunk.forEach { customTheme ->
                        val isActive = activeThemeString == "custom:${customTheme.id}"
                        AppThemeCard(
                            data = ThemeCardData.Downloaded(customTheme),
                            isActive = isActive,
                            onAction = {
                                scopedSettings?.let { settings ->
                                    settings.theme = "custom:${customTheme.id}"
                                }
                                ThemeController.setTheme(ThemeConfig.Custom(customTheme))
                            },
                            onDelete = {
                                downloadedThemeManager.removeTheme(customTheme.id)
                                downloadedThemes = downloadedThemeManager.getAllThemes()
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (chunk.size < 2) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Text(
            text = "Theme Store",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else if (storeThemes.isEmpty()) {
            Text(
                text = "No themes available in the store.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            storeThemes.chunked(2).forEach { chunk ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                ) {
                    chunk.forEach { appTheme ->
                        val isDownloaded = downloadedThemeManager.getTheme(appTheme.id) != null
                        val isActive = isDownloaded && activeThemeString == "custom:${appTheme.id}"
                        val screenshotUrls = appTheme.attributes.screenshots.map { it.url }

                        AppThemeCard(
                            data = ThemeCardData.StoreItem(appTheme, screenshotUrls),
                            isActive = isActive,
                            onAction = {
                                scope.launch {
                                    val customTheme = themeDownloader.download(
                                        downloadLink = appTheme.attributes.downloadLink,
                                        themeName = appTheme.attributes.name,
                                        themeId = appTheme.id,
                                    )
                                    if (customTheme != null) {
                                        downloadedThemeManager.addTheme(customTheme)
                                        downloadedThemes = downloadedThemeManager.getAllThemes()
                                        scopedSettings?.let { settings ->
                                            settings.theme = "custom:${customTheme.id}"
                                        }
                                        ThemeController.setTheme(ThemeConfig.Custom(customTheme))
                                        KurozoraLogger.debug("[ThemeStoreContent]", "Downloaded and applied theme: ${customTheme.name}")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (chunk.size < 2) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
