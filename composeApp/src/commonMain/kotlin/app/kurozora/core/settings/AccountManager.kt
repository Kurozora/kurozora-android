package app.kurozora.core.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kurozorakit.api.AccountUser
import kurozorakit.shared.logging.KurozoraLogger

class AccountManager(
    private val settingsManager: SettingsManager,
) {
    private val _activeAccount = MutableStateFlow<AccountUser?>(null)
    val activeAccount: StateFlow<AccountUser?> = _activeAccount.asStateFlow()

    init {
        val activeId = settingsManager.getActiveAccountId()
        _activeAccount.value = activeId?.let { settingsManager.getAccountById(it) }
        if (activeId != null) {
            KurozoraLogger.info("[AccountManager]", "Active account loaded: $activeId")
        } else {
            KurozoraLogger.info("[AccountManager]", "No active account found")
        }
    }

    fun getAllAccounts(): List<AccountUser> = settingsManager.getAccounts()

    fun addAccount(account: AccountUser) {
        settingsManager.addOrUpdateAccount(account)
        KurozoraLogger.info("[AccountManager]", "Account added: ${account.id}")
        if (_activeAccount.value == null) switchAccount(account.id)
    }

    fun updateActiveAccount(profileUrl: String, userJson: String? = null) {
        _activeAccount.update { it?.copy(profileUrl = profileUrl, userJson = userJson ?: it.userJson) }
        val account = _activeAccount.value ?: return
        settingsManager.addOrUpdateAccount(account)
        KurozoraLogger.debug("[AccountManager]", "Active account updated: ${account.id}")
    }

    fun switchAccount(id: String) {
        val user = settingsManager.getAccountById(id) ?: return
        settingsManager.setActiveAccountId(id)
        _activeAccount.value = user
        KurozoraLogger.info("[AccountManager]", "Switched to account: $id")
    }

    fun removeAccount(id: String) {
        settingsManager.removeAccount(id)
        if (_activeAccount.value?.id == id) _activeAccount.value = null
        KurozoraLogger.info("[AccountManager]", "Account removed: $id")
    }

    fun getScopedSettings(): AccountScopedSettings? {
        val id = _activeAccount.value?.id ?: return null
        return settingsManager.getAccountScopedSettings(id)
    }

    fun logout() {
        _activeAccount.value?.id ?: return
        settingsManager.setActiveAccountId(null)
        _activeAccount.value = null
        KurozoraLogger.info("[AccountManager]", "User logged out")
    }
}
