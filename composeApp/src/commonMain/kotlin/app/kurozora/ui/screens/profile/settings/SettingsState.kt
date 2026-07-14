package app.kurozora.ui.screens.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.kurozora.core.icons.getDisplayNameForIcon
import app.kurozora.core.settings.AccountScopedSettings
import app.kurozora.ui.screens.library.imports.MALImport
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import kotlinx.datetime.TimeZone
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.TVRating
import kurozorakit.data.models.misc.LibraryImport
import kurozorakit.data.models.theme.app.AppTheme
import kurozorakit.data.models.user.User
import kurozorakit.shared.logging.LogLevel
import kurozorakit.shared.logging.LogPacket
import kurozorakit.shared.logging.MemoryBufferSink
import org.koin.compose.koinInject

data class SettingsState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    val username: String = "",
    val email: String = "",
    val bio: String = "",

    val profileImageFile: PlatformFile? = null,
    val profileImageBytes: ByteArray? = null,
    val profileImageUrl: String? = null,
    val bannerImageFile: PlatformFile? = null,
    val bannerImageBytes: ByteArray? = null,
    val bannerImageUrl: String? = null,

    val language: String = "English",
    val timezone: String = "Europe/Istanbul",
    val tvRating: TVRating = TVRating.R_15,

    val importLibraryKind: KKLibrary.Kind = KKLibrary.Kind.SHOWS,
    val importLibraryService: LibraryImport.Service = LibraryImport.Service.MAL,
    val importLibraryBehavior: LibraryImport.Behavior = LibraryImport.Behavior.MERGE,
    val importLibraryFile: PlatformFile? = null,
    val importLibraryFileContent: String? = null,
    val importLibraryBytes: ByteArray? = null,

    val currentPassword: TextFieldValue = TextFieldValue(""),
    val newPassword: TextFieldValue = TextFieldValue(""),
    val confirmPassword: TextFieldValue = TextFieldValue(""),

    val isSaving: Boolean = false,

    val isTwoFactorEnabled: Boolean = false,

    val storeThemeItems: List<AppTheme> = emptyList(),
    val isLoadingStoreThemes: Boolean = false,
)

// Görseldeki her bir "Save" veya aksiyon butonu için ayrı bir Event
sealed interface SettingsEvent {
    data class UpdateUsername(val value: String) : SettingsEvent
    data class UpdateEmail(val value: String) : SettingsEvent
    data class UpdateBio(val value: String) : SettingsEvent
    data class UpdateLanguage(val value: String) : SettingsEvent
    data class UpdateTimezone(val value: String) : SettingsEvent
    data class UpdateTVRating(val value: TVRating) : SettingsEvent


    data class UpdateImportLibraryKind(val value: KKLibrary.Kind) : SettingsEvent
    data class UpdateImportLibraryService(val value: LibraryImport.Service) : SettingsEvent
    data class UpdateImportLibraryBehavior(val value: LibraryImport.Behavior) : SettingsEvent
    data class SelectImportLibraryFile(val file: PlatformFile) : SettingsEvent
    data class SaveImportLibraryBytes(val bytes: ByteArray) : SettingsEvent

    data class SelectProfileImage(val file: PlatformFile) : SettingsEvent
    data class SelectBannerImage(val file: PlatformFile) : SettingsEvent

//    data class ToggleTwoFactor(val isEnabled: Boolean) : SettingsEvent

    data object SaveAccountDetails : SettingsEvent
    data object SavePicture : SettingsEvent
    data object SaveBio : SettingsEvent
    data object SaveLanguage : SettingsEvent
    data object SaveTimezone : SettingsEvent
    data object SaveTVRating : SettingsEvent
    data object ImportLibrary : SettingsEvent
    data object SavePassword : SettingsEvent
    data object EnableTwoFactor : SettingsEvent
    data object LogoutOtherSessions : SettingsEvent
    data object DeleteAccount : SettingsEvent

    // Advanced / Debug
    data object ClearLogBuffer : SettingsEvent

    // Theme Store
    data object LoadThemeStore : SettingsEvent
    data class DownloadStoreTheme(val appTheme: AppTheme) : SettingsEvent
    data class DeleteDownloadedTheme(val themeId: String) : SettingsEvent
}

data class SettingsCategory(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val icon: @Composable (() -> Unit)? = null, // opsiyonel icon
    val items: List<SettingItem>,
)

sealed class SettingItem(
    open val key: String,
    open val title: String,
    open val subtitle: String? = null,
    open val content: @Composable (() -> Unit)? = null,
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
        val onValueChanged: (String) -> Unit = {},
        val supportsSearch: Boolean = false
    ) : SettingItem(key, title, subtitle)

    data class MultiSelectSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val options: List<String>,
        val selected: List<String>,
        val onValueChanged: (List<String>) -> Unit = {}
    ) : SettingItem(key, title, subtitle)

    data class CustomSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val contentWithState: @Composable (SettingsState) -> Unit = {},  // Yeni isim - state parametreli
        override val content: @Composable (() -> Unit)? = null,  // override ediyoruz, null olabilir
        val isFullDialog: Boolean = false,
        val onClick: (() -> Unit)? = null,
    ) : SettingItem(key, title, subtitle, content)

    data class TextInputSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val value: String,
        val isMultiline: Boolean = false, // Bio gibi alanlar için
        val placeholder: String = "",
        val onValueChanged: (String) -> Unit
    ) : SettingItem(key, title, subtitle)

    data class ImagePickerSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val currentImageUrl: String? = null,
        val onImageSelected: (PlatformFile) -> Unit
    ) : SettingItem(key, title, subtitle)

    data class ActionButtonSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val buttonText: String,
        val isDestructive: Boolean = false, // Hesabı sil gibi kırmızı butonlar için
        val onClick: () -> Unit
    ) : SettingItem(key, title, subtitle)

    data class FilePickerSetting(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val buttonText: String,
        val fileExtensions: List<String> = listOf("xml"), // MAL/AniList genelde XML veya JSON kullanır
        val onFileSelected: (PlatformFile) -> Unit
    ) : SettingItem(key, title, subtitle)
}

fun generateSettingsCategories(
    scopedSettings: AccountScopedSettings,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateToBlockedUsers: () -> Unit = {},
): List<SettingsCategory> {
    return listOf(
        SettingsCategory(
            key = "account_details",
            title = "Account",
            subtitle = "Manage your account.",
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
            items = listOf(
                SettingItem.ImagePickerSetting(
                    key = "profile_picture",
                    title = "Profile picture",
                    subtitle = "JPG, GIF or PNG. Max size of 800K",
                    currentImageUrl = state.profileImageUrl,
                    onImageSelected = { file ->
                        onEvent(SettingsEvent.SelectProfileImage(file))
                        onEvent(SettingsEvent.SavePicture)
                    }
                ),
                SettingItem.ImagePickerSetting(
                    key = "banner_picture",
                    title = "Banner picture",
                    subtitle = "JPG, GIF or PNG. Max size of 800K",
                    currentImageUrl = state.bannerImageUrl,
                    onImageSelected = { file ->
                        onEvent(SettingsEvent.SelectBannerImage(file))
                        onEvent(SettingsEvent.SavePicture)
                    }
                ),
                SettingItem.TextInputSetting(
                    key = "username",
                    title = "Username",
                    placeholder = "Username",
                    value = state.username,
                    onValueChanged = { onEvent(SettingsEvent.UpdateUsername(it)) }
                ),
                SettingItem.TextInputSetting(
                    key = "bio",
                    title = "Bio",
                    placeholder = "Write something about yourself...",
                    isMultiline = true,
                    value = state.bio,
                    onValueChanged = { onEvent(SettingsEvent.UpdateBio(it)) }
                ),
                SettingItem.CustomSetting(
                    key = "blocked_users",
                    title = "Blocked Users",
                    subtitle = "Manage users you have blocked",
                    onClick = onNavigateToBlockedUsers,
                    content = null,
                )
            )
        ),
        SettingsCategory(
            key = "general",
            title = "General",
            subtitle = "Basic app settings",
            icon = { Icon(Icons.Default.Settings, contentDescription = "General") },
            items = listOf(
                SettingItem.CustomSetting(
                    key = "about_libraries",
                    title = "Open Source Licenses",
                    subtitle = "See all open source libraries used in this app.",
                    isFullDialog = true,
                    content = {
                        val libraries by produceLibraries {
                            Res.readBytes("files/aboutlibraries.json").decodeToString()
                        }
                        LibrariesContainer(libraries, Modifier.fillMaxSize())
                    }
                )
            )
        ),
        SettingsCategory(
            key = "appearance",
            title = "Appearance",
            subtitle = "Customize your app appearance.",
            icon = { Icon(Icons.Default.Palette, contentDescription = "Appearance") },
            items = listOf(
                SettingItem.CustomSetting(
                    key = AccountScopedSettings.APP_ICON_KEY,
                    title = "App Icon",
                    subtitle = getDisplayNameForIcon(scopedSettings.icon),
                    isFullDialog = true,
                    contentWithState = {
                        AppIconPickerContent(scopedSettings)
                    },
                    content = null,
                ),
                SettingItem.SingleSelectSetting(
                    key = "timezone_selection",
                    title = "Timezone",
                    subtitle = "Select your local timezone",
                    options = TimeZone.availableZoneIds
                        .filter { it.contains("/") }
                        .sorted(),
                    selected = state.timezone.ifEmpty { TimeZone.currentSystemDefault().id },
                    onValueChanged = { selectedTimezone ->
                        onEvent(SettingsEvent.UpdateTimezone(selectedTimezone))
                        onEvent(SettingsEvent.SaveTimezone)
                    },
                    supportsSearch = true
                ),
                SettingItem.SingleSelectSetting(
                    key = "tvrating_selection",
                    title = "TV Rating",
                    options = TVRating.entries.map { it.displayName },
                    selected = state.tvRating.displayName,
                    onValueChanged = { selected ->
                        val rating = TVRating.entries.find { it.displayName == selected } ?: TVRating.ALL_AGES
                        onEvent(SettingsEvent.UpdateTVRating(rating))
                        onEvent(SettingsEvent.SaveTVRating)
                    },
                ),
                SettingItem.CustomSetting(
                    key = AccountScopedSettings.THEME_KEY,
                    title = "Theme",
                    subtitle = when {
                        scopedSettings.theme.startsWith("custom:") -> "Custom theme"
                        else -> scopedSettings.theme.replaceFirstChar { it.uppercase() }
                    },
                    isFullDialog = true,
                    contentWithState = { currentState ->
                        ThemeStoreContent(currentState)
                    },
                    content = null,
                ),
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.LANGUAGE_KEY,
                    title = "Language",
                    subtitle = "Choose your app language.",
                    options = listOf("en", "tr", "jp"),
                    selected = scopedSettings.language
                ),
            )
        ),
        SettingsCategory(
            key = "library",
            title = "Library",
            subtitle = "Import your library from MyAnimeList or AniList.",
            icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "Library") },
            items = listOf(
                SettingItem.SingleSelectSetting(
                    key = "library_import_kind",
                    title = "Library Kind",
                    subtitle = "What kind of library do you want to import?",
                    options = KKLibrary.Kind.entries.toList().map { it.name },
                    selected = state.importLibraryKind.name,
                ),
                SettingItem.SingleSelectSetting(
                    key = "library_import_service",
                    title = "Library Service",
                    subtitle = "",
                    options = LibraryImport.Service.entries.toList().map { it.name },
                     selected = state.importLibraryService.name,
                ),
                SettingItem.SingleSelectSetting(
                    key = "library_import_behavior",
                    title = "Library Behavior",
                    subtitle = "",
                    options = LibraryImport.Behavior.entries.toList().map { it.name },
                    selected = state.importLibraryBehavior.name,
                ),
                SettingItem.FilePickerSetting(
                    key = "library_import_xml",
                    title = "Library Import",
                    subtitle = "Import your list from MyAnimeList or AniList.",
                    buttonText = "Select File",
                    fileExtensions = listOf("xml"),
                    onFileSelected = { file ->
                        onEvent(SettingsEvent.SelectImportLibraryFile(file))
                    }
                ),
                SettingItem.CustomSetting(
                    key = "import_list",
                    title = "Import Preview",
                    subtitle = "Preview your imported list",
                    isFullDialog = true,
                    contentWithState = { currentState ->  // state parametreli versiyon
                        ImportPreviewContent(
                            xmlContent = currentState.importLibraryFileContent.orEmpty(),
                            onExport = { bytes ->
                                onEvent(SettingsEvent.SaveImportLibraryBytes(bytes))
                                onEvent(SettingsEvent.ImportLibrary)
                            }
                        )
                    },
                    content = null
                )
            )
        ),
        SettingsCategory(
            key = "advanced",
            title = "Advanced",
            subtitle = "Debug and logging configuration",
            icon = { Icon(Icons.Default.BugReport, contentDescription = "Advanced") },
            items = listOf(
                SettingItem.CustomSetting(
                    key = "log_viewer",
                    title = "View Logs",
                    subtitle = "View live application logs",
                    isFullDialog = true,
                    contentWithState = { currentState ->
                        LogViewerContent(currentState)
                    }
                ),
                SettingItem.ActionButtonSetting(
                    key = "clear_log_buffer",
                    title = "Clear Log Buffer",
                    buttonText = "Clear",
                    onClick = { onEvent(SettingsEvent.ClearLogBuffer) }
                )
            )
        ),
        SettingsCategory(
            key = "app_info",
            title = "App Info",
            subtitle = "Application information",
            icon = { Icon(Icons.Default.Info, contentDescription = "App Info") },
            items = listOf(
                SettingItem.CustomSetting(
                    key = "version_info",
                    title = "App Version",
                    subtitle = "1.0.0"
                )
            )
        ),
    )
}

@Composable
fun ImportPreviewContent(
    xmlContent: String,
    onExport: (ByteArray) -> Unit
) {
    println("ImportPreviewContent - XML content length: ${xmlContent.length}")

    if (xmlContent.isNotEmpty()) {
        val animeResult = remember(xmlContent) {
            println("Parsing XML content of length: ${xmlContent.length}")
            try {
                MALImport.Anime.parse(xmlContent)
            } catch (e: Exception) {
                println("Parse error: ${e.message}")
                null
            }
        }

        animeResult?.let { result ->
            println("Parse successful. Found ${result.animeList.size} anime entries")
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Found ${result.animeList.size} anime entries",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                MALImport.UI.AnimeListScreen(
                    animeResult = result,
                    onExport = onExport
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Failed to parse XML content", color = MaterialTheme.colorScheme.error)
                    Text(
                        text = "First 200 chars: ${xmlContent.take(200)}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select an XML file first")
        }
    }
}

@Composable
fun LogViewerContent(state: SettingsState) {
    val sink: MemoryBufferSink = koinInject()
    val logList = remember { mutableStateOf(sink.snapshot()) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        sink.logFlow.collect { packets ->
            logList.value = packets
            if (listState.canScrollForward.not()) {
                listState.animateScrollToItem(packets.size - 1)
            }
        }
    }

    val filteredLogs = logList.value.filter { it.tag != "[HTTP]" }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${filteredLogs.size} entries", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { sink.clear() }) {
                Text("Clear")
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(filteredLogs) { packet ->
                Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "${packet.level.name} [${packet.tag}] ${packet.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = when (packet.level) {
                            LogLevel.VERBOSE -> MaterialTheme.colorScheme.onSurfaceVariant
                            LogLevel.DEBUG -> MaterialTheme.colorScheme.onSurface
                            LogLevel.INFO -> MaterialTheme.colorScheme.primary
                            LogLevel.WARN -> MaterialTheme.colorScheme.tertiary
                            LogLevel.ERROR -> MaterialTheme.colorScheme.error
                            LogLevel.ASSERT -> MaterialTheme.colorScheme.error
                            LogLevel.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}