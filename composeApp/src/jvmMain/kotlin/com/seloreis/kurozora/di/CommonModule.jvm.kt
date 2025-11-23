package com.seloreis.kurozora.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.seloreis.kurozora.core.settings.AccountManager
import com.seloreis.kurozora.core.settings.SettingsManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
//    single<Settings> { Settings() } // JVM’de in-memory veya file-backed
//    single { SettingsManager(get()) }          // factory yok
//    single { AccountManager(get()) }
}