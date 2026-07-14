package app.kurozora.di

import app.kurozora.core.icons.AppIconManager
import kurozorakit.store.DatabaseDriverFactory
import kurozorakit.store.JvmDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { JvmDatabaseDriverFactory("./kurozora.db") }
    single { AppIconManager() }
}
