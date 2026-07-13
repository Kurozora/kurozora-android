package app.kurozora

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.kurozora.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kurozorakit.store.DatabaseDriverFactory
import kurozorakit.store.db.KurozoraDatabase
import org.koin.android.ext.koin.androidContext

class KurozoraApplication : Application() {
    val loggingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        initKoin {
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
