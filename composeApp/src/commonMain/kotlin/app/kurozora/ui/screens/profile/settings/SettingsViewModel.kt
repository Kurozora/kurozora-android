package app.kurozora.ui.screens.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.core.settings.AccountManager
import app.kurozora.core.theme.DownloadedThemeManager
import app.kurozora.core.theme.ThemeDownloader
import app.kurozora.ui.theme.ThemeConfig
import app.kurozora.ui.theme.ThemeController
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.TVRating
import kurozorakit.data.models.media.Media
import kurozorakit.data.models.user.User
import kurozorakit.data.models.user.update.UserUpdate
import kurozorakit.shared.logging.KurozoraLogger
import kurozorakit.shared.logging.MemoryBufferSink

class SettingsViewModel(
    private val kurozoraKit: KurozoraKit,
    private val accountManager: AccountManager,
    private val memoryBufferSink: MemoryBufferSink,
    private val downloadedThemeManager: DownloadedThemeManager,
    private val themeDownloader: ThemeDownloader,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            accountManager.activeAccount.collect { account ->
                account?.let {
                    val user = Json.decodeFromString<User>(account.userJson)
                    _state.update {
                        it.copy(
                            user = user,
                            username = user.attributes.username,
                            email = user.attributes.email ?: "",
                            bio = user.attributes.biography ?: "",
                            profileImageUrl = user.attributes.profile?.url,
                            bannerImageUrl = user.attributes.banner?.url
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateUsername -> _state.update { it.copy(username = event.value) }
            is SettingsEvent.UpdateEmail -> _state.update { it.copy(email = event.value) }
            is SettingsEvent.UpdateBio -> _state.update { it.copy(bio = event.value) }
            is SettingsEvent.UpdateLanguage -> _state.update { it.copy(language = event.value) }
            is SettingsEvent.UpdateTimezone -> _state.update { it.copy(timezone = event.value) }
            is SettingsEvent.UpdateTVRating -> _state.update { it.copy(tvRating = event.value) }

            is SettingsEvent.UpdateImportLibraryKind -> _state.update { it.copy(importLibraryKind = event.value) }
            is SettingsEvent.UpdateImportLibraryService -> _state.update { it.copy(importLibraryService = event.value) }
            is SettingsEvent.UpdateImportLibraryBehavior -> _state.update { it.copy(importLibraryBehavior = event.value) }
            is SettingsEvent.SelectImportLibraryFile -> {
                _state.update { it.copy(importLibraryFile = event.file) }
                importLibraryFile()
            }
            is SettingsEvent.SaveImportLibraryBytes -> _state.update { it.copy(importLibraryBytes = event.bytes) }

            is SettingsEvent.SelectProfileImage -> _state.update { it.copy(profileImageFile = event.file) }
            is SettingsEvent.SelectBannerImage -> _state.update { it.copy(bannerImageFile = event.file) }

            SettingsEvent.SaveAccountDetails -> saveField(UserUpdate(biography = state.value.bio, profile = state.value.profileImageBytes, banner = state.value.bannerImageBytes))
            SettingsEvent.SavePicture -> { savePicture() }
            SettingsEvent.SaveBio -> saveField(UserUpdate(biography = state.value.bio))
            SettingsEvent.SaveLanguage -> saveField(UserUpdate(preferredLanguage = state.value.language))
            SettingsEvent.SaveTimezone -> saveField(UserUpdate(preferredTimezone = state.value.timezone))
            SettingsEvent.SaveTVRating -> saveField(UserUpdate(preferredTVRating = state.value.tvRating.rawValue))
            SettingsEvent.ImportLibrary -> { startLibraryImport() }
            SettingsEvent.SavePassword -> { }
            SettingsEvent.EnableTwoFactor -> { }
            SettingsEvent.LogoutOtherSessions -> { }
            SettingsEvent.DeleteAccount -> { }

            SettingsEvent.ClearLogBuffer -> {
                memoryBufferSink.clear()
            }

            SettingsEvent.LoadThemeStore -> loadThemeStore()
            is SettingsEvent.DownloadStoreTheme -> downloadStoreTheme(event.appTheme)
            is SettingsEvent.DeleteDownloadedTheme -> deleteDownloadedTheme(event.themeId)
        }
    }

    private fun loadThemeStore() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingStoreThemes = true) }
            kurozoraKit.themeStore().getThemeStore()
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            storeThemeItems = response.data,
                            isLoadingStoreThemes = false,
                        )
                    }
                }.onError { error ->
                    KurozoraLogger.error("[SettingsViewModel]", "Failed to load theme store", error)
                    _state.update { it.copy(isLoadingStoreThemes = false) }
                }
        }
    }

    private fun downloadStoreTheme(appTheme: kurozorakit.data.models.theme.app.AppTheme) {
        viewModelScope.launch {
            val customTheme = themeDownloader.download(
                downloadLink = appTheme.attributes.downloadLink,
                themeName = appTheme.attributes.name,
                themeId = appTheme.id,
            )
            if (customTheme != null) {
                downloadedThemeManager.addTheme(customTheme)
                KurozoraLogger.debug("[SettingsViewModel]", "Downloaded theme: ${customTheme.name}")
                val scopedSettings = accountManager.getScopedSettings()
                scopedSettings?.let { settings ->
                    settings.theme = "custom:${customTheme.id}"
                }
                ThemeController.setTheme(ThemeConfig.Custom(customTheme))
            }
        }
    }

    private fun deleteDownloadedTheme(themeId: String) {
        downloadedThemeManager.removeTheme(themeId)
        KurozoraLogger.debug("[SettingsViewModel]", "Deleted theme: $themeId")
    }

    private fun saveField(userUpdate: UserUpdate) {
        KurozoraLogger.debug("[SettingsViewModel]", "saveField()")
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            kurozoraKit.user().updateMyProfile(update = userUpdate).
            onSuccess { res ->
                val data = res.data
                _state.update {
                    it.copy(
                        isSaving = false,
                        username = data.username ?: it.username,
                        bio = data.biography ?: it.bio,
                        profileImageUrl = data.profileImageURL ?: it.profileImageUrl,
                        bannerImageUrl = data.bannerImageURL ?: it.bannerImageUrl,
                        language = data.preferredLanguage ?: it.language,
                        timezone = data.preferredTimezone ?: it.timezone,
                        tvRating = TVRating.entries.find { r -> r.rawValue == data.preferredTVRating } ?: it.tvRating,
                        successMessage = res.message
                    )
                }
                val user = _state.value.user ?: return@onSuccess
                data.username?.let { user.attributes.username = it }
                data.biography?.let { user.attributes.biography = it }
                data.preferredLanguage?.let { user.attributes.preferredLanguage = it }
                data.preferredTVRating?.let { user.attributes.preferredTVRating = it }
                data.preferredTimezone?.let { user.attributes.preferredTimezone = it }
                val profileImageURL = data.profileImageURL
                if (profileImageURL != null) {
                    user.attributes.profile = Media(url = profileImageURL)
                }
                val bannerImageURL = data.bannerImageURL
                if (bannerImageURL != null) {
                    user.attributes.banner = Media(url = bannerImageURL)
                }
                val newUserJson = Json.encodeToString(user)
                accountManager.updateActiveAccount(
                    profileUrl = data.profileImageURL ?: "",
                    userJson = newUserJson
                )
            }.onError { error ->
                KurozoraLogger.error("[SettingsViewModel]", "saveField failed", error)
                _state.update { it.copy(isSaving = false, errorMessage = "Failed to save: $error") }
            }
        }
    }

    private fun savePicture() {
        viewModelScope.launch {
            val profileBytes = _state.value.profileImageFile?.readBytes()
            val bannerBytes = _state.value.bannerImageFile?.readBytes()
            _state.update { it.copy(profileImageBytes = profileBytes, bannerImageBytes = bannerBytes) }
        }
    }

    private fun importLibraryFile() {
        KurozoraLogger.debug("[SettingsViewModel]", "importLibraryFile()")
        viewModelScope.launch {
            val file = _state.value.importLibraryFile
            if (file != null) {
                try {
                    val content = file.readString()
                    KurozoraLogger.debug("[SettingsViewModel]", "File content loaded. Length: ${content.length}")
                    KurozoraLogger.debug("[SettingsViewModel]", "First 200 chars: ${content.take(200)}")

                    _state.update { it.copy(importLibraryFileContent = content) }

                    KurozoraLogger.debug("[SettingsViewModel]", "State updated. New content length: ${_state.value.importLibraryFileContent?.length}")

                } catch (e: Exception) {
                    KurozoraLogger.error("[SettingsViewModel]", "Error reading file", e)
                    _state.update { it.copy(errorMessage = "Failed to read file: ${e.message}") }
                }
            }
        }
    }

    private fun startLibraryImport() {
        KurozoraLogger.debug("[SettingsViewModel]", "startLibraryImport()")
        viewModelScope.launch {
            val file = _state.value.importLibraryFile
            if (file != null) {
                val bytes = file.readBytes()

                _state.update { it.copy(isSaving = true) }

                kurozoraKit.user().importToLibrary(
                    libraryKind = _state.value.importLibraryKind,
                    service = _state.value.importLibraryService,
                    behavior = _state.value.importLibraryBehavior,
                    xmlBytes = bytes
                ).onSuccess {
                    _state.update { it.copy(isSaving = false, successMessage = "Import successful!") }
                }.onError {
                    _state.update { it.copy(isSaving = false, errorMessage = "Import failed.") }
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
