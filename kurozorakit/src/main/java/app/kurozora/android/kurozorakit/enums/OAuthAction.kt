package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class OAuthAction(val value: String) {
    signIn("signIn"),
    setupAccount("setupAccount");
}
