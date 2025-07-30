package app.kurozora.tracker.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AttributesSize(val sizeName : String , val sizeDp : Dp ) {
    Banner("banner" , 128.dp),
    Upcoming("upcoming" , 156.dp),
    Video("video" , 224.dp),
    Small("small" , 156.dp),
    Medium("medium" , 164.dp ),
    Large("large" , 192.dp );

    companion object {
        fun getSize(size: String) = values().find{ it.sizeName == size }?.sizeDp ?: Medium.sizeDp
    }
}