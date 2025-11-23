package app.kurozora.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.core.settings.AccountManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit

class MainViewModel(
    private val kit: KurozoraKit,
    private val accountManager: AccountManager,
) : ViewModel() {
    private val _seenOnboarding = MutableStateFlow(false)
    val seenOnboarding: StateFlow<Boolean> = _seenOnboarding

    init {
        // Örn: SharedPreferences veya DataStore’dan oku
        viewModelScope.launch {
            val seen = accountManager.getScopedSettings()?.get("seenOnboarding").toBoolean()
            _seenOnboarding.value = seen
        }
    }

    fun markOnboardingSeen() {
        viewModelScope.launch {
            accountManager.getScopedSettings()?.set("seenOnboarding", "true")
            _seenOnboarding.value = true
        }
    }

    init {
//        print("HELLO WORLD")
//        println(accountManager.activeAccount.value)
//        println( "--------------------------------")
//        runBlocking {
//            accountManager.getAllAccounts().forEach { accountUser ->
//                println("Accounts:")
//                println("Accounts \n" + accountUser)
//            }
//            launch {
//                accountManager.activeAccount.collect { user ->
//                    println("Active account changed: $user")
//                }
//            }
        //accountManager.removeAccount("24509")
//            accountManager.addAccount(AccountUser(
//                id = "24509",
//                token = "24509|kurozora_HFzWKvM9woNPymSvvEbv2lFhOIqxgCQ1bQM3sALH84c2bb00",
//                username = "selman",
//            ))
//            accountManager.switchAccount("1")
    }

    //        viewModelScope.launch {
//            accountManager.getAllAccounts().forEach { accountUser ->
//                println("Accounts:")
//                println("Account" + accountUser)
//            }
//            accountManager.activeAccount.collect { activeAccount ->
//                _authState.value = if (activeAccount != null) AuthState.LOGGED_IN
//                else AuthState.LOGGED_OUT
//            }
//        }
//    }
//    init {
//        viewModelScope.launch {
//            kit.show().getShows(listOf("1", "22521", "22308", "30232", "30087")).onSuccess {
//                println("SHOWS BATCH REQUEST $it")
//            }
//        }
//    }
    init {
        // Hesap ekle
//        accountManager.addAccount(
//            AccountUser(
//                id = "1",
//                token = "abc123",
//                username = "selman",
//                profileUrl = "https://example.com/avatar.jpg"
//            )
//        )
//
//        // Hesaplar
//        val all = accountManager.getAllAccounts()
//
//        // Aktif hesap değiştir
//        accountManager.switchAccount("1")
//
//        // Aktif kullanıcının scoped settings’ine eriş
//        val scoped = accountManager.getScopedSettings()
//        scoped?.theme = "dark"
//        println(scoped?.token)
    }
}
