package app.kurozora.android.kurozorakit.enums

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class ActivityStatus(val stringValue: String, val symbolValue: String, val colorValue: Color) {
    // MARK: - Cases
    /// The user is currently online.
    online("Online", "✓", Color.Green),

    /// The user was recently online.
    seenRecently("Seen Recently", "–", Color.Yellow),

    /// The user is offline.
    offline("Offline", "x", Color.Red)
}
