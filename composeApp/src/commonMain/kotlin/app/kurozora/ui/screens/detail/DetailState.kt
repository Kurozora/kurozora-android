package app.kurozora.ui.screens.detail

import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import kurozorakit.data.models.library.LibraryAttributes
import kurozorakit.data.models.media.MediaStat
import kurozorakit.data.models.review.Review
import kurozorakit.data.models.show.attributes.AiringStatus

data class DetailData(
    val itemType: ItemType,
    val windowWidth: WindowWidthSizeClass,
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val synopsisTitle: String? = null,
    val synopsis: String,
    val coverImageUrl: String,
    val bannerImageUrl: String? = null,
    val stats: Map<String, String>,
    val infos: List<InfoCard>,
    val mediaStat: MediaStat?,
    val status: AiringStatus? = null,
    val library: LibraryAttributes? = null,
    val reviews: List<Review> = emptyList(),
)

data class InfoCard(
    val title: String,
    val value: String,
    val subtitle: String,
)
