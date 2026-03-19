package app.kurozora.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule(), commonModule())
    }

// called by iOS etc
// fun initKoin() = initKoin(enableNetworkLogs = false) {}
fun KoinApplication.Companion.start(): KoinApplication = initKoin { }
