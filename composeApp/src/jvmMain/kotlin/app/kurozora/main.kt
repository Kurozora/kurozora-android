package app.kurozora

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.kurozora.di.initKoin
import app.kurozora.ui.screens.main.MainScreen
import org.koin.core.Koin

lateinit var koin: Koin
fun main() {
    koin = initKoin(enableNetworkLogs = true).koin

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kurozora",
        ) {
            //App()
            MainScreen()
        }
    }
}
