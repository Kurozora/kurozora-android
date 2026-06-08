package app.kurozora.ui.screens.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.core.settings.AccountManager
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.user.User
import kurozorakit.data.models.user.update.UserUpdate

class SettingsViewModel(
    private val kurozoraKit: KurozoraKit,
    private val accountManager: AccountManager
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
            SettingsEvent.SavePassword -> { /* Şifre güncelleme Ktor isteği */ }
            SettingsEvent.EnableTwoFactor -> { /* 2FA aktifleştirme */ }
            SettingsEvent.LogoutOtherSessions -> { /* Session kapatma */ }
            SettingsEvent.DeleteAccount -> { /* Hesap silme uyarı dialogu tetikleme */ }
        }
    }

    private fun saveField(userUpdate: UserUpdate) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            kurozoraKit.user().updateMyProfile(update = userUpdate).
            onSuccess { res ->
                println("res: $res")
                accountManager.updateActiveAccount(profileUrl = res.data.profileImageURL ?: "")
                _state.update { it.copy(isSaving = false, profileImageUrl = res.data.profileImageURL, bannerImageUrl = res.data.bannerImageUrl, successMessage = res.message) }
            }.onError { error ->
                println("$userUpdate failed to save.")
                _state.update { it.copy(isSaving = false) }
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
        viewModelScope.launch {
            val file = _state.value.importLibraryFile
            if (file != null) {
                try {
                    val content = file.readString()
                    println("File content loaded. Length: ${content.length}")
                    println("First 200 chars: ${content.take(200)}")

                    _state.update { it.copy(importLibraryFileContent = content) }

                    // State güncellendi mi kontrol et
                    println("State updated. New content length: ${_state.value.importLibraryFileContent?.length}")

                } catch (e: Exception) {
                    println("Error reading file: ${e.message}")
                    _state.update { it.copy(errorMessage = "Failed to read file: ${e.message}") }
                }
            }
        }
    }

    private fun startLibraryImport() {
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