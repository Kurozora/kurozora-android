package app.kurozora

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.kurozora.di.initKoin
import kurozorakit.store.DatabaseDriverFactory
import kurozorakit.store.db.KurozoraDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class KurozoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger(level = Level.NONE)
            androidContext(androidContext = this@KurozoraApplication)
        }
    }
}

class AndroidDatabaseDriverFactory(
    private val context: Context
) : DatabaseDriverFactory {

    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KurozoraDatabase.Schema,
            context = context,
            name = "kurozora.db"
        )
    }
}
