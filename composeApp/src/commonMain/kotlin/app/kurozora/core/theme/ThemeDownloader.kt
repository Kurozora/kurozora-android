package app.kurozora.core.theme

import app.kurozora.BuildKonfig
import app.kurozora.core.settings.AccountManager
import app.kurozora.getPlatform
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import app.kurozora.ui.theme.CustomTheme
import kurozorakit.shared.UserAgent
import kurozorakit.shared.logging.KurozoraLogger

class ThemeDownloader(
    private val accountManager: AccountManager,
) {
    private val userAgent = UserAgent(
        appName = "KtorClient",
        appVersion = "1.0.0",
        appID = "com.seloreis.kurozora",
        platformName = getPlatform().platform,
        platformVersion = getPlatform().platformVersion,
    )

    private val client = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    val token = runCatching { accountManager.activeAccount.value?.token }.getOrNull()
                    if (token != null) BearerTokens(token, "") else null
                }
            }
        }

        defaultRequest {
            header("X-API-Key", BuildKonfig.API_KEY)
            header(HttpHeaders.UserAgent, "${userAgent.getAppName()}/${userAgent.getAppVersion()} (${userAgent.getAppID()}; ${userAgent.getPlatformName()} ${userAgent.getPlatformVersion()}) KtorClient/3.2.2")
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
        }
    }

    suspend fun download(downloadLink: String, themeName: String = "Unknown", themeId: String = "0"): CustomTheme? {
        return try {
            val response: HttpResponse = client.get(downloadLink)
            val body = response.bodyAsText()
            KurozoraLogger.debug("[ThemeDownloader]", "Download response received, length: ${body.length}")

            val global = extractPlistSection(body, "Global")
            val tableView = extractPlistSection(body, "TableViewCell")
            val topLevel = extractPlistKeyValues(body)

            val id = themeId
            val name = topLevel["Name"] ?: themeName
            val version = topLevel["Version"] ?: "1.0"

            val theme = CustomTheme(
                id = id,
                name = name,
                version = version,
                globalTint = global["tintColor"] ?: "#FF9300",
                globalBackground = global["backgroundColor"] ?: "#353A50",
                tableViewBackground = tableView["backgroundColor"] ?: global["textFieldBackgroundColor"] ?: "#454F63",
                borderColor = global["borderColor"] ?: global["separatorColor"] ?: "#AFAFAF",
                tintedBackground = global["tintedBackgroundColor"] ?: "#50577D",
                tintedButtonText = global["tintedButtonTextColor"] ?: "#EEEEEE",
                textColor = global["textColor"] ?: "#EEEEEE",
                subTextColor = global["subTextColor"] ?: "#AFAFAF",
            )

            KurozoraLogger.debug("[ThemeDownloader]", "Parsed theme: ${theme.name} v${theme.version}")
            theme
        } catch (e: Exception) {
            KurozoraLogger.error("[ThemeDownloader]", "Failed to download theme from $downloadLink", e)
            null
        }
    }

    private fun extractPlistSection(xml: String, section: String): Map<String, String> {
        val sectionRegex = Regex("<key>$section</key>\\s*<dict>([\\s\\S]*?)</dict>")
        val sectionMatch = sectionRegex.find(xml) ?: return emptyMap()
        val content = sectionMatch.groupValues[1]
        return extractPlistKeyValues(content)
    }

    private fun extractPlistKeyValues(xml: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val kvRegex = Regex("<key>([^<]+)</key>\\s*<string>([^<]*)</string>")
        kvRegex.findAll(xml).forEach { match ->
            result[match.groupValues[1]] = match.groupValues[2]
        }
        return result
    }
}
