package app.kurozora.ui.screens.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
import kurozorakit.data.models.user.User

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
        val isFullDialog: Boolean = false
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
    onEvent: (SettingsEvent) -> Unit
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
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.APP_ICON_KEY,
                    title = "App Icon",
                    subtitle = "Choose your app icon.",
                    options = listOf("default", "halloween", "kuro-chan"),
                    selected = scopedSettings.icon
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
                        val rating = TVRating.entries.find { it.name == selected } ?: TVRating.ALL_AGES
                        onEvent(SettingsEvent.UpdateTVRating(rating))
                        onEvent(SettingsEvent.SaveTVRating)
                    },
                ),
                SettingItem.SingleSelectSetting(
                    key = AccountScopedSettings.THEME_KEY,
                    title = "Theme",
                    subtitle = "Choose your app theme.",
                    options = listOf("default", "black", " day", "grass", "night", "sakura", "sky"),
                    selected = scopedSettings.theme
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
                    selected = state.importLibraryKind.name.lowercase(),
                ),
                SettingItem.SingleSelectSetting(
                    key = "library_import_service",
                    title = "Library Service",
                    subtitle = "",
                    options = LibraryImport.Service.entries.toList().map { it.name },
                     selected = state.importLibraryService.name.lowercase(),
                ),
                SettingItem.SingleSelectSetting(
                    key = "library_import_behavior",
                    title = "Library Behavior",
                    subtitle = "",
                    options = LibraryImport.Behavior.entries.toList().map { it.name },
                    selected = state.importLibraryBehavior.name.lowercase(),
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