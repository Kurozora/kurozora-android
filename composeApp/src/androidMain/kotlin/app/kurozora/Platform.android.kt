package app.kurozora

import android.os.Build
import kurozorakit.api.Platform

class AndroidPlatform : Platform {
    override val platform = "Android"
    override val platformVersion = Build.VERSION.RELEASE ?: "Unknown"
    override val deviceVendor = Build.MANUFACTURER ?: "Unknown"
    override val deviceModel = Build.MODEL ?: "Unknown"
}

actual fun getPlatform(): Platform = AndroidPlatform()
