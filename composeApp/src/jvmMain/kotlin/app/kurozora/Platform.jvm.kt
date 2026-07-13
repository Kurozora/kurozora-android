package app.kurozora

import kurozorakit.api.Platform

class JVMPlatform : Platform {
    override val platform = System.getProperty("os.name") ?: "JVM"
    override val platformVersion = (System.getProperty("os.version") ?: "1.0.0")
        .takeWhile { it.isDigit() || it == '.' }
        .ifEmpty { "1.0.0" }
    override val deviceVendor = System.getProperty("java.vendor") ?: "Unknown"
    override val deviceModel = System.getProperty("os.arch") ?: "Unknown"
}

actual fun getPlatform(): Platform = JVMPlatform()
