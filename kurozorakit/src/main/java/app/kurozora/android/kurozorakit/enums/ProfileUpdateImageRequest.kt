package app.kurozora.android.kurozorakit.enums

sealed class ProfileUpdateImageRequest {
    data class update(val url: String?) : ProfileUpdateImageRequest()
    object delete : ProfileUpdateImageRequest()
}
