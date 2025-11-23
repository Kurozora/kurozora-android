package app.kurozora

import kurozorakit.api.Platform

class JVMPlatform : Platform {
    override val platform = "Windows"

    //System.getProperty("os.name") ?: "JVM"
    override val platformVersion = System.getProperty("os.version") ?: "Unknown"
    override val deviceVendor = System.getProperty("java.vendor") ?: "Unknown"
    override val deviceModel = System.getProperty("os.arch") ?: "Unknown"
}

actual fun getPlatform(): Platform = JVMPlatform()
