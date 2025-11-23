package com.seloreis.kurozora

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seloreis.kurozora.di.initKoin
import com.seloreis.kurozora.ui.screens.main.MainScreen
import org.koin.core.Koin

lateinit var koin: Koin

fun main() {
    koin = initKoin(enableNetworkLogs = true).koin

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KurozoraApp",
        ) {
            //App()
            MainScreen()
        }
    }
}