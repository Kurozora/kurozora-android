package app.kurozora.di

import android.content.Context
import app.kurozora.AndroidDatabaseDriverFactory
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kurozorakit.store.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidSettingsFactory(context: Context): Settings.Factory =
    SharedPreferencesSettings.Factory(context)

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
}
