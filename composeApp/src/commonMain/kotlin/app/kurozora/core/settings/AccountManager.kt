package app.kurozora.core.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kurozorakit.api.AccountUser

class AccountManager(
    private val settingsManager: SettingsManager,
) {
    private val _activeAccount = MutableStateFlow<AccountUser?>(null)
    val activeAccount: StateFlow<AccountUser?> = _activeAccount.asStateFlow()

    init {
        val activeId = settingsManager.getActiveAccountId()
        _activeAccount.value = activeId?.let { settingsManager.getAccountById(it) }
    }

    fun getAllAccounts(): List<AccountUser> = settingsManager.getAccounts()
    fun addAccount(account: AccountUser) {
        settingsManager.addOrUpdateAccount(account)
        if (_activeAccount.value == null) switchAccount(account.id)
    }

    fun switchAccount(id: String) {
        val user = settingsManager.getAccountById(id) ?: return
        settingsManager.setActiveAccountId(id)
        _activeAccount.value = user
    }

    fun removeAccount(id: String) {
        settingsManager.removeAccount(id)
        if (_activeAccount.value?.id == id) _activeAccount.value = null
    }

    fun getScopedSettings(): AccountScopedSettings? {
        val id = _activeAccount.value?.id ?: return null
        return settingsManager.getAccountScopedSettings(id)
    }

    fun logout() {
        _activeAccount.value?.id ?: return
//        val scoped = getScopedSettings()
//        scoped?.clear()
        settingsManager.setActiveAccountId(null)
        _activeAccount.value = null
    }
}
