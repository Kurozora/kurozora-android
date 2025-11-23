package com.seloreis.kurozora.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.seloreis.kurozora.core.settings.AccountManager
import com.seloreis.kurozora.core.settings.SettingsManager
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidSettingsFactory(context: Context): Settings.Factory =
    SharedPreferencesSettings.Factory(context)

actual fun platformModule(): Module = module {
//    single<Settings.Factory> { androidSettingsFactory(get()) }
//    single<Settings> { get<Settings.Factory>().create("kurozora_root") }
//    single { SettingsManager(get(), get()) }
//    single { AccountManager(get()) }
}