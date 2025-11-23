package app.kurozora.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
//    single<Settings> { Settings() } // JVM’de in-memory veya file-backed
//    single { SettingsManager(get()) }          // factory yok
//    single { AccountManager(get()) }
}
