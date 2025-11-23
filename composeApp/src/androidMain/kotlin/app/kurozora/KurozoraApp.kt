package app.kurozora

import android.app.Application
import app.kurozora.di.initKoin
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
