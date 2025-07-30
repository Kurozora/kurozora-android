package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class UserNotificationType(val value: String) {
    session("NewSession"),
    follower("NewFollower"),
    feedMessageReply("NewFeedMessageReply"),
    feedMessageReShare("NewFeedMessageReShare"),
    libraryImportFinished("LibraryImportFinished"),
    subscriptionStatus("SubscriptionStatus"),
    other("");
}
