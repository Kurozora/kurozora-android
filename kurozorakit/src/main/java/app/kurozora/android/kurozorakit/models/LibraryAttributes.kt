package app.kurozora.android.kurozorakit.models

import app.kurozora.android.kurozorakit.enums.FavoriteStatus
import app.kurozora.android.kurozorakit.enums.HiddenStatus
import app.kurozora.android.kurozorakit.enums.KKLibrary
import app.kurozora.android.kurozorakit.enums.ReminderStatus
import kotlinx.serialization.Serializable

typealias LibraryUpdate = LibraryAttributes

@Serializable
data class LibraryAttributes(
    var rating: Double? = null,
    var review: String? = null,
    var isFavorited: Boolean? = null,
    private var _favoriteStatus: FavoriteStatus? = null,
    var isReminded: Boolean? = null,
    private var _reminderStatus: ReminderStatus? = null,
    var isHidden: Boolean? = null,
    private var _hiddenStatus: HiddenStatus? = null,
    var status: KKLibrary.Status? = null,
    var rewatchCount: Int? = null,
) {
    // MARK: - Properties
    var favoriteStatus: FavoriteStatus
        get() = _favoriteStatus ?: FavoriteStatus.fromBoolean(isFavorited)
        set(value) {
            _favoriteStatus = value
        }
    var reminderStatus: ReminderStatus
        get() = _reminderStatus ?: ReminderStatus.fromBoolean(isReminded)
        set(value) {
            _reminderStatus = value
        }
    var hiddenStatus: HiddenStatus
        get() = _hiddenStatus ?: HiddenStatus.fromBoolean(isHidden)
        set(value) {
            _hiddenStatus = value
        }

    // MARK: - Functions
    fun update(libraryUpdate: LibraryUpdate) {
        favoriteStatus = libraryUpdate.favoriteStatus
        reminderStatus = libraryUpdate.reminderStatus
        status = libraryUpdate.status
    }

    fun updated(libraryUpdate: LibraryUpdate): LibraryAttributes {
        return this.copy(
            _favoriteStatus = libraryUpdate.favoriteStatus,
            _reminderStatus = libraryUpdate.reminderStatus,
            status = libraryUpdate.status
        )
    }
}
