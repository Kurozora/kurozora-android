package app.kurozora.core.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json
import kurozorakit.api.AccountUser

class SettingsManager(
    private val rootSettings: Settings,
    private val factory: Settings.Factory? = null, // platform'da sağlayabilirsin, opsiyonel
) {
    companion object {
        private const val ACCOUNTS_KEY = "app.kurozora.accounts"        // JSON array of AccountUser
        private const val ACTIVE_ACCOUNT_ID_KEY = "app.kurozora.active_account_id"
    }

    private val json = Json { ignoreUnknownKeys = true }
    fun getAccounts(): List<AccountUser> {
        val raw = rootSettings.getStringOrNull(ACCOUNTS_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<AccountUser>>(raw) }.getOrDefault(emptyList())
    }

    private fun putAccounts(accounts: List<AccountUser>) {
        rootSettings[ACCOUNTS_KEY] = json.encodeToString(accounts)
    }

    fun addOrUpdateAccount(account: AccountUser) {
        val accounts = getAccounts().toMutableList()
        val idx = accounts.indexOfFirst { it.id == account.id }
        if (idx >= 0) accounts[idx] = account else accounts.add(account)
        putAccounts(accounts)
    }

    fun removeAccount(id: String) {
        val accounts = getAccounts().filterNot { it.id == id }
        putAccounts(accounts)
        if (getActiveAccountId() == id) {
            rootSettings.remove(ACTIVE_ACCOUNT_ID_KEY)
        }
        // Ayrıca namespaced keys temizlenecek; AccountScopedSettings.clear() yapabilirsiniz.
        getAccountScopedSettings(id).clear()
    }

    fun getActiveAccountId(): String? = rootSettings.getStringOrNull(ACTIVE_ACCOUNT_ID_KEY)
    fun setActiveAccountId(id: String?) {
        if (id == null) rootSettings.remove(ACTIVE_ACCOUNT_ID_KEY) else rootSettings[ACTIVE_ACCOUNT_ID_KEY] = id
    }

    fun getAccountById(id: String): AccountUser? = getAccounts().firstOrNull { it.id == id }
    fun clearAll() = rootSettings.clear()

    /**
     * Eski createChild yerine:
     * - Eğer platform factory (Settings.Factory) enjekte edildi ise factory.create(name) döndür.
     * - Yoksa NamespacedAccountScopedSettings ile prefix tabanlı fallback sağlar.
     */
    fun getAccountScopedSettings(accountId: String): AccountScopedSettings {
        return if (factory != null) {
            // Eğer factory sağlayabilirseniz platform-unudaki named settings'i kullanın:
            AccountScopedSettings(factory.create("user_$accountId"), namespace = null)
        } else {
            // Fallback: tek root Settings ile namespaced keys kullan
            AccountScopedSettings(rootSettings, namespace = "user.$accountId")
        }
    }
}
