package app.kurozora.core.icons

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

class AndroidAppIconManager(
    private val context: Context,
) : AppIconManager() {

    override fun getCurrentIcon(): String? {
        val tag = "AppIconManager"
        val packageManager = context.packageManager
        val packageName = context.packageName
        val classPrefix = resolveClassPrefix()
        val allIcons = getAllIconIdentifiers()
        val defaultComponent = ComponentName(packageName, "${classPrefix}_default")

        for (iconId in allIcons) {
            val component = ComponentName(packageName, "${classPrefix}_${sanitizeClassName(iconId)}")
            val state = packageManager.getComponentEnabledSetting(component)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                Log.d(tag, "getCurrentIcon: found ENABLED = $iconId")
                return iconId
            }
        }

        if (packageManager.getComponentEnabledSetting(defaultComponent) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            Log.d(tag, "getCurrentIcon: default alias DEFAULT (manifest-enabled)")
            return "default"
        }

        Log.d(tag, "getCurrentIcon: no enabled alias found")
        return null
    }

    override fun setIcon(identifier: String) {
        val tag = "AppIconManager"
        val packageManager = context.packageManager
        val packageName = context.packageName
        val safeIdentifier = sanitizeClassName(identifier)
        val classPrefix = resolveClassPrefix()

        val allIcons = getAllIconIdentifiers()
        val allAliases = allIcons.map { iconId ->
            ComponentName(packageName, "${classPrefix}_${sanitizeClassName(iconId)}")
        }
        val targetAlias = allAliases.firstOrNull { it.className.endsWith("_$safeIdentifier") }
            ?: run { Log.e(tag, "Alias not found for: $identifier"); return }
        val targetName = targetAlias.className.substringAfterLast(".")

        var currentEnabledAlias: ComponentName? = null
        for (c in allAliases) {
            val state = packageManager.getComponentEnabledSetting(c)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                || (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                    && c == allAliases.firstOrNull { it.className.endsWith("_default") })
            ) {
                currentEnabledAlias = c
                Log.d(tag, "Current enabled: ${c.className.substringAfterLast(".")} (state=$state)")
                break
            }
        }

        if (targetAlias != currentEnabledAlias) {
            Log.d(tag, ">>> ENABLING $targetName")
            packageManager.setComponentEnabledSetting(
                targetAlias,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP,
            )

            if (currentEnabledAlias != null) {
                val curName = currentEnabledAlias.className.substringAfterLast(".")
                Log.d(tag, ">>> DISABLING $curName")
                packageManager.setComponentEnabledSetting(
                    currentEnabledAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                )
            }

            Log.d(tag, "Icon switch complete — process stays alive, launcher will update via broadcast")
        } else {
            Log.d(tag, "Already set to $targetName, nothing to do")
        }
    }

    private fun resolveClassPrefix(): String {
        val packageManager = context.packageManager
        val packageName = context.packageName
        var classPrefix = "$packageName.MainActivity"
        try {
            val pkgInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            pkgInfo.activities?.forEach { a ->
                if (a.name.endsWith(".MainActivity")) {
                    classPrefix = a.name
                }
            }
        } catch (_: Exception) {
        }
        return classPrefix
    }

    private fun sanitizeClassName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9_]"), "_")
    }

    private fun getAllIconIdentifiers(): List<String> {
        return getAllAppIconCategories().flatMap { it.icons.map { icon -> icon.identifier } }
    }
}
