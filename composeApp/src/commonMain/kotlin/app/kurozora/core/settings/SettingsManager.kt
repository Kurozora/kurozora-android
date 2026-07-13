package app.kurozora.core.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json
import kurozorakit.api.AccountUser
import kurozorakit.shared.logging.KurozoraLogger

class SettingsManager(
    private val rootSettings: Settings,
    private val factory: Settings.Factory? = null,
) {
    companion object {
        private const val ACCOUNTS_KEY = "app.kurozora.accounts"
        private const val ACTIVE_ACCOUNT_ID_KEY = "app.kurozora.active_account_id"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun getAccounts(): List<AccountUser> {
        val raw = rootSettings.getStringOrNull(ACCOUNTS_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<AccountUser>>(raw) }.onFailure { e ->
            KurozoraLogger.error("[SettingsManager]", "Failed to deserialize accounts", e)
        }.getOrDefault(emptyList())
    }

    private fun putAccounts(accounts: List<AccountUser>) {
        rootSettings[ACCOUNTS_KEY] = json.encodeToString(accounts)
    }

    fun addOrUpdateAccount(account: AccountUser) {
        val accounts = getAccounts().toMutableList()
        val idx = accounts.indexOfFirst { it.id == account.id }
        if (idx >= 0) accounts[idx] = account else accounts.add(account)
        putAccounts(accounts)
        KurozoraLogger.debug("[SettingsManager]", "Account persisted: ${account.id}")
    }

    fun removeAccount(id: String) {
        val accounts = getAccounts().filterNot { it.id == id }
        putAccounts(accounts)
        if (getActiveAccountId() == id) {
            rootSettings.remove(ACTIVE_ACCOUNT_ID_KEY)
        }
        getAccountScopedSettings(id).clear()
        KurozoraLogger.info("[SettingsManager]", "Account removed from persistence: $id")
    }

    fun getActiveAccountId(): String? = rootSettings.getStringOrNull(ACTIVE_ACCOUNT_ID_KEY)

    fun setActiveAccountId(id: String?) {
        if (id == null) rootSettings.remove(ACTIVE_ACCOUNT_ID_KEY) else rootSettings[ACTIVE_ACCOUNT_ID_KEY] = id
    }

    fun getAccountById(id: String): AccountUser? = getAccounts().firstOrNull { it.id == id }

    fun clearAll() {
        rootSettings.clear()
        KurozoraLogger.warning("[SettingsManager]", "All settings cleared")
    }

    fun getAccountScopedSettings(accountId: String): AccountScopedSettings {
        return if (factory != null) {
            AccountScopedSettings(factory.create("user_$accountId"), namespace = null)
        } else {
            AccountScopedSettings(rootSettings, namespace = "user.$accountId")
        }
    }
}
